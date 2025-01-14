package cn.lbcmmszdntnt.websocket.session;


import cn.lbcmmszdntnt.common.util.session.SessionMap;
import jakarta.websocket.Session;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 15:34
 */
public class WsSessionMapper {

    private final static SessionMap<Session> SESSION_MAP = new SessionMap<>();

    public static void put(String sessionKey, Session webSocketService) {
        SESSION_MAP.put(sessionKey, webSocketService);
    }

    public static Session get(String sessionKey) {
        return SESSION_MAP.get(sessionKey);
    }

    public static boolean containsKey(String sessionKey) {
        return SESSION_MAP.containsKey(sessionKey);
    }

    public static void remove(String sessionKey) {
        SESSION_MAP.remove(sessionKey);
    }

    public static int size(String prefix) {
        return SESSION_MAP.size(prefix);
    }

    public static Set<String> getKeys(String prefix) {
        return SESSION_MAP.getKeys(prefix);
    }

    public static void consumeKey(String sessionKey, Consumer<Session> consumer) {
        SESSION_MAP.consumeKey(sessionKey, consumer);
    }

    public static void consumePrefix(String prefix, Consumer<Session> consumer) {
        SESSION_MAP.consumePrefix(prefix, consumer);
    }

    public static void removeAll(String prefix) {
        SESSION_MAP.removeAll(prefix);
    }

}
