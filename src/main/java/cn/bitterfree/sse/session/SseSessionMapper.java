package cn.bitterfree.sse.session;

import cn.bitterfree.common.util.session.SessionMap;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 15:34
 */
public class SseSessionMapper {

    private final static SessionMap<SseEmitter> SESSION_MAP = new SessionMap<>();

    public static void put(String sessionKey, SseEmitter sseEmitter) {
        SESSION_MAP.put(sessionKey, sseEmitter);
    }

    public static SseEmitter get(String sessionKey) {
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

    public static void removeAll(String prefix) {
        SESSION_MAP.removeAll(prefix);
    }

}
