package cn.lbcmmszdntnt.redis.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-13
 * Time: 21:09
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisMapCache {

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisCache redisCache;

    private final RedisCacheSerializer redisCacheSerializer;

    public <HK, HV> void putAll(final String key, final Map<HK, HV> map) {
        Map<String, String> jsonMap = redisCacheSerializer.toJson(map);
        log.info("Map 存入 Redis\t[{}]-[{}]", key, jsonMap);
        redisTemplate.opsForHash().putAll(key, jsonMap);
    }

    public <HK, HV> void putAllOver(final String key, final Map<HK, HV> map) {
        redisCache.deleteObject(key);
        putAll(key, map);
    }

    public <HK, HV> void init(final String key, final Map<HK, HV> map, long timeout, final TimeUnit timeUnit) {
        putAllOver(key, map);
        redisCache.expire(key, timeout, timeUnit);
    }

    public <HK, HV> Optional<Map<HK, HV>> getMap(final String key, final Class<HK> hkClazz, final Class<HV> hvClazz) {
        Map<String, String> jsonMap = redisTemplate.opsForHash().entries(key).entrySet().stream().collect(Collectors.toMap(
                entry -> String.valueOf(entry.getKey()),
                entry -> String.valueOf(entry.getValue()),
                (oldData, newData) -> newData
        ));
        log.info("获取 Redis 中的 Map 缓存\t[{}]-[{}]", key, jsonMap);
        Map<HK, HV> map = redisCacheSerializer.parse(jsonMap, hkClazz, hvClazz);
        return Optional.ofNullable(map).filter(m -> !CollectionUtils.isEmpty(m));
    }

    public <HK, HV> void put(final String key, final HK hashKey, final HV hashValue) {
        String jsonHashKey = redisCacheSerializer.toJson(hashKey);
        String jsonHashValue = redisCacheSerializer.toJson(hashValue);
        log.info("存入 Redis 的某个 Map\t[{}.{}]-[{}]", key, jsonHashKey, jsonHashValue);
        redisTemplate.opsForHash().put(key, jsonHashKey, jsonHashValue);
    }

    public <HK, HV> Optional<HV> get(final String key, final HK hashKey, final Class<HV> hvClazz) {
        String jsonHashKey = redisCacheSerializer.toJson(hashKey);
        String hashValue = (String) redisTemplate.opsForHash().get(key, jsonHashKey);
        log.info("获取 Redis 中的 Map 的键值\t[{}.{}]-[{}]", key, key, hashValue);
        return Optional.ofNullable(redisCacheSerializer.parse(hashValue, hvClazz));
    }

    public <HK> long increment(final String key, final HK hashKey, final long delta) {
        String jsonHashKey = redisCacheSerializer.toJson(hashKey);
        long number = redisTemplate.opsForHash().increment(key, jsonHashKey, delta);
        log.info("Redis key[{}.{}] {} 后：{}", key, jsonHashKey, delta, number);
        return number;
    }

    public <HK> void remove(final String key, final HK hashKey) {
        String jsonHashKey = redisCacheSerializer.toJson(hashKey);
        log.info("删除 Redis 中的 Map 的键值\tkey[{}.{}]", key, jsonHashKey);
        redisTemplate.opsForHash().delete(key, jsonHashKey);
    }

    public <HK> Boolean containsKey(final String key, final HK hashKey) {
        String jsonHashKey = redisCacheSerializer.toJson(hashKey);
        Boolean flag = redisTemplate.opsForHash().hasKey(key, jsonHashKey);
        log.info("查询 Redis 的 Map 的键值是否存在\t[{}.{}]-[{}]", key, jsonHashKey, flag);
        return flag;
    }

}
