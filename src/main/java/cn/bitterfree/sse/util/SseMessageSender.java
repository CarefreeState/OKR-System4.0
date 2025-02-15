package cn.bitterfree.sse.util;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.util.convert.JsonUtil;
import cn.bitterfree.common.util.juc.threadpool.ThreadPoolUtil;
import cn.bitterfree.sse.session.SseSessionMapper;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 9:53
 */
@Slf4j
public class SseMessageSender {

    private final static ThreadPoolExecutor EXECUTOR = ThreadPoolUtil.getIoTargetThreadPool("SSE-Thread");;

    public static <T> void sendMessage(String sessionKey, Supplier<T> messageSupplier) {
        EXECUTOR.submit(() -> {
            T message = messageSupplier.get();
            log.info("服务器 -> [{}] text: {}", sessionKey, message);
            if(!SseSessionMapper.containsKey(sessionKey)) {
                log.warn(GlobalServiceStatusCode.SSE_CONNECTION_NOT_EXIST.getMessage());
                return;
            }
            SseEmitter sseEmitter = SseSessionMapper.get(sessionKey);
            try {
                sseEmitter.send(JsonUtil.toJson(message), MediaType.APPLICATION_JSON);
            } catch (Exception e) {
                log.error("{} 发送消息异常 {}", sessionKey, e.getMessage());
                SseSessionMapper.remove(sessionKey);
            }
        });
    }

    public static <T> void sendMessage(String sessionKey, T message) {
        sendMessage(sessionKey, () -> message);
    }

    public static <T> void sendAllMessage(Set<String> sessionKeys, Function<String, T> function) {
        if (Collections.isEmpty(sessionKeys)) {
            return;
        }
        sessionKeys.stream().parallel().distinct().forEach(sessionKey -> {
            sendMessage(sessionKey, function.apply(sessionKey));
        });
    }

    public static <T> void sendAllMessage(String prefix, Function<String, T> function) {
        sendAllMessage(SseSessionMapper.getKeys(prefix), function);
    }

}
