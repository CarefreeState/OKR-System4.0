package cn.lbcmmszdntnt.sse.util;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.sse.session.SseSessionMapper;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 9:53
 */
@Slf4j
public class SseMessageSender {

    private final static MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON;

    private static Consumer<IOException> handleException(String sessionKey) {
        return e -> {
            log.error("{} 发送消息异常 {}", sessionKey, e.getMessage());
            SseSessionMapper.remove(sessionKey);
        };
    }

    private static <T> void sendMessage(SseEmitter sseEmitter, T message, Consumer<IOException> handleException) {
        if(Objects.isNull(sseEmitter)) {
            log.warn(GlobalServiceStatusCode.SSE_CONNECTION_NOT_EXIST.toString());
            return;
        }
        try {
            sseEmitter.send(JsonUtil.toJson(message), MEDIA_TYPE);
        } catch (IOException e) {
            handleException.accept(e);
        }
    }

    public static <T> void sendMessage(String sessionKey, T message) {
        log.info("服务器 -> [{}] text: {}", sessionKey, message);
        SseSessionMapper.consumeKey(sessionKey, sseEmitter -> {
            sendMessage(sseEmitter, message, handleException(sessionKey));
        });
    }

    public static <T> void sendAllMessage(String prefix, Function<String, T> function) {
        SseSessionUtil.getSessionKeys(prefix).stream().parallel().forEach(sessionKey -> {
            sendMessage(sessionKey, function.apply(sessionKey));
        });
    }

    public static <T> void sendAllMessage(List<String> sessionKeys, Function<String, T> function) {
        if (Collections.isEmpty(sessionKeys)) {
            return;
        }
        sessionKeys.stream().parallel().distinct().forEach(sessionKey -> {
            sendMessage(sessionKey, function.apply(sessionKey));
        });
    }

}
