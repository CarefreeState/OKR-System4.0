package cn.bitterfree.api.sse.util;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.common.util.convert.DateTimeUtil;
import cn.bitterfree.api.interceptor.handler.ext.after.ContextClearAfterHandler;
import cn.bitterfree.api.sse.session.SseSessionMapper;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.function.Consumer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 13:59
 */
@Slf4j
public class SseSessionUtil {

    private final static String DEFAULT_MESSAGE = "OK";

    private final static ContextClearAfterHandler CONTEXT_CLEAR_AFTER_HANDLER = SpringUtil.getBean(ContextClearAfterHandler.class);

    private static Runnable completionCallBack(String sessionKey) {
        return () -> {
            log.info("{} 结束 SSE 连接", sessionKey);
            SseSessionMapper.remove(sessionKey);
        };
    }

    private static Runnable timeOutCallBack(String sessionKey) {
        return () -> {
            log.warn("{} 连接 SSE 超时", sessionKey);
            SseSessionMapper.remove(sessionKey);
        };
    }

    private static Consumer<Throwable> errorCallBack(String sessionKey) {
        return throwable -> {
            log.error("{} 连接 SSE 异常", sessionKey);
            SseSessionMapper.remove(sessionKey);
        };
    }

    public static SseEmitter createConnect(long timeout, String sessionKey) {
        try {
            // 超时时间设置为 timeout ms，0 表示不过期，默认是 30 秒，超过时间未完成会抛出异常（不设置时间代表不限时）
            SseEmitter sseEmitter = new SseEmitter(timeout);
            log.warn("{} 成功建立连接，将于 {} ms 后断开连接，即 {}", sessionKey, timeout,
                    DateTimeUtil.getDateFormat(new Date(System.currentTimeMillis() + timeout)));
            if(SseSessionMapper.containsKey(sessionKey)) {
                throw new GlobalServiceException(GlobalServiceStatusCode.SSE_CONNECTION_IS_EXIST);
            }
            // 注册回调
            sseEmitter.onCompletion(completionCallBack(sessionKey));
            sseEmitter.onTimeout(timeOutCallBack(sessionKey)); // 超时后重新连接但会抛出 AsyncRequestTimeoutException 异常
            sseEmitter.onError(errorCallBack(sessionKey));
            SseSessionMapper.put(sessionKey, sseEmitter);
            // 发送一个确认字符串，让客户端尽快地确认和建立 sse 连接
            SseMessageSender.sendMessage(sessionKey, DEFAULT_MESSAGE);
            return sseEmitter;
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage(), GlobalServiceStatusCode.SSE_CONNECTION_CREATE_FAILED);
        } finally {
            // 创建连接后即可清除本地线程变量（避免影响使用这个线程的其他请求）
            CONTEXT_CLEAR_AFTER_HANDLER.action(null, null, null);
        }
    }

    /**
     * 获取当前连接总数
     *
     * @return
     */
    public static int getConnectTotal(String prefix) {
        return SseSessionMapper.size(prefix);
    }

}
