package cn.lbcmmszdntnt.websocket.util;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.websocket.session.WsSessionMapper;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 9:53
 */
@Slf4j
public class WsMessageSender {

    public static <T> void sendMessage(Session session, T message) {
        if(Objects.isNull(session)) {
            log.warn(GlobalServiceStatusCode.USER_NOT_ONLINE.toString());
            return;
        }
        try {
            synchronized (session) {
                if(session.isOpen()) {
                    session.getBasicRemote().sendText(JsonUtil.toJson(message));
                }
            }
        } catch (IOException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    /**
     * 实现服务器主动推送
     */
    public static <T> void sendMessageToOne(String sessionKey, T message) {
        log.info("服务器 -> [{}] text: {}", sessionKey, message);
        WsSessionMapper.consumeKey(sessionKey, session -> {
            sendMessage(session, message);
        });
    }

    /**
     * 实现服务器主动推送（群发）(希望消息之间抛异常不影响)
     */
    public static <T> void sendMessageToAll(String prefix, T message) {
        log.info("服务器 -> [{}*] text: {}", prefix, message);
        WsSessionMapper.consumePrefix(prefix, session -> {
            sendMessage(session, message);
        });
    }

}
