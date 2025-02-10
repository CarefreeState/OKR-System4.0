package cn.bitterfree.redis.cache;

import cn.bitterfree.common.util.convert.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-13
 * Time: 19:49
 */
@Component
public class RedisCacheSerializer {

    public <T> T parse(String json, Class<T> clazz) {
        return JsonUtil.parse(json, clazz);
    }

    public String toJson(Object obj) {
        return JsonUtil.toJson(obj);
    }

    public <K, V> Map<String, String> toJson(Map<K, V> map) {
        Map<String, String> jsonMap = new HashMap<>();
        Optional.ofNullable(map)
                .stream()
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .forEach(entry -> {
                    jsonMap.put(toJson(entry.getKey()), toJson(entry.getValue()));
                });
        return jsonMap;
    }

    public <K, V> Map<K, V> parse(Map<String, String> jsonMap, Class<K> kClazz, Class<V> vClazz) {
        Map<K, V> map = new HashMap<>();
        Optional.ofNullable(jsonMap)
                .stream()
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .forEach(entry -> {
                    map.put(parse(entry.getKey(), kClazz), parse(entry.getValue(), vClazz));
                });
        return map;
    }

    public <E> Set<String> toJson(Set<E> set) {
        return Optional.ofNullable(set)
                .stream()
                .flatMap(Set::stream)
                .map(this::toJson)
                .collect(Collectors.toSet());
    }

    public <E> Set<E> parse(Set<String> jsonSet, Class<E> eClazz) {
        return Optional.ofNullable(jsonSet)
                .stream()
                .flatMap(Set::stream)
                .map(e -> parse(e, eClazz))
                .collect(Collectors.toSet());
    }

    public <E> List<String> toJson(List<E> list) {
        return Optional.ofNullable(list)
                .stream()
                .flatMap(List::stream)
                .map(this::toJson)
                .collect(Collectors.toList());
    }

    public <E> List<E> parse(List<String> jsonList, Class<E> eClazz) {
        return Optional.ofNullable(jsonList)
                .stream()
                .flatMap(List::stream)
                .map(e -> parse(e, eClazz))
                .collect(Collectors.toList());
    }
}
