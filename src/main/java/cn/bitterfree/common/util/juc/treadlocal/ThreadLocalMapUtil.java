package cn.bitterfree.common.util.juc.treadlocal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-14
 * Time: 0:34
 */
@Slf4j
public class ThreadLocalMapUtil {

    private final static ThreadLocal<Map<String, Object>> THREAD_LOCAL = new ThreadLocal<>();

    private static Map<String, Object> getMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if(Objects.isNull(map)) {
            map = new HashMap<>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void removeAll() {
        log.debug("{} 移除本地线程资源", Thread.currentThread().getName());
        THREAD_LOCAL.remove();
    }

    public static void remove(String key) {
        log.debug("{} 移除本地线程资源 {}", Thread.currentThread().getName(), key);
        getMap().remove(key);
    }

    public static <T> void set(String key, T value) {
        Map<String, Object> map = getMap();
        map.put(key, value);
        log.debug("{} 设置本地线程资源 [{}.{}]", Thread.currentThread().getName(), key, value);
    }

    public static Object get(String key) {
        Map<String, Object> map = getMap();
        Object value = map.get(key);
        log.debug("{} 获取本地线程资源 [{}.{}]", Thread.currentThread().getName(), key, value);
        return value;
    }

    public static <T> T get(String key, Class<T> clazz) {
        return clazz.cast(get(key));
    }

    public static <T> T get(String key, Function<Object, T> mapper) {
        Object value = get(key);
        return Objects.isNull(value) ? null : mapper.apply(value);
    }

    public static void append(String key, String str) {
        String newStr = Optional.ofNullable(get(key, String.class))
                .filter(StringUtils::hasText)
                .map(s -> s + str)
                .orElse(str)
        ;
        set(key, newStr);
    }

}
