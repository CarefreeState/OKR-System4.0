package cn.bitterfree.api.redis.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisCache {

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisCacheSerializer redisCacheSerializer;

    public Boolean expire(final String key, final long timeout, final TimeUnit timeUnit) {
        log.info("为 Redis 的键值设置超时时间\t[{}]-[{}  {}]", key, timeout, timeUnit.name());
        return redisTemplate.expire(key, timeout, timeUnit);
    }

    public long getKeyTTL(final String key, final TimeUnit timeUnit) {
        int ttl = redisTemplate.opsForValue().getOperations().getExpire(key).intValue();
        String message = switch (ttl) {
            case -1 -> "没有设置过期时间";
            case -2 -> "key 不存在";
            default -> ttl + TimeUnit.SECONDS.name();
        };
        log.info("查询 Redis key[{}] 剩余存活时间:{}", key, message);
        return timeUnit.convert(ttl, TimeUnit.SECONDS);
    }

    public <V> void setObject(final String key, final V value) {
        String jsonValue = redisCacheSerializer.toJson(value);
        log.info("存入 Redis\t[{}]-[{}]", key, jsonValue);
        redisTemplate.opsForValue().set(key, jsonValue);
    }

    public <V> void setObject(final String key, final V value, final long timout, final TimeUnit timeUnit) {
        String jsonValue = redisCacheSerializer.toJson(value);
        log.info("存入 Redis\t[{}]-[{}]，超时时间:[{}  {}]", key, jsonValue, timout, timeUnit.name());
        redisTemplate.opsForValue().set(key, jsonValue, timout, timeUnit);
    }

    public <V> Optional<V> getObject(final String key, final Class<V> vClazz) {
        String jsonValue = redisTemplate.opsForValue().get(key);
        log.info("查询 Redis\t[{}]-[{}]", key, jsonValue);
        return Optional.ofNullable(redisCacheSerializer.parse(jsonValue, vClazz));
    }

    public Boolean deleteObject(final String key) {
        log.info("删除 Redis 的键值\tkey[{}]", key);
        return redisTemplate.delete(key);
    }

    public void deleteObjects(final Collection<String> keys) {
        log.info("删除 Redis 的键值\tkeys[{}]", keys);
        redisTemplate.delete(keys);
    }

    public Long decrement(final String key) {
        Long number = redisTemplate.opsForValue().decrement(key);
        log.info("Redis key[{}] 自减后：{}", key, number);
        return number;
    }

    public Long increment(final String key) {
        Long number = redisTemplate.opsForValue().increment(key);
        log.info("Redis key[{}] 自增后：{}", key, number);
        return number;
    }

    public Boolean isExists(final String key) {
        Boolean flag = redisTemplate.hasKey(key);
        log.info("查询 Redis 的键值是否存在\t[{}]-[{}]", key, flag);
        return flag;
    }

    /**
     * key 的类型在这个方法限制为 String
     * 获得缓存的基本对象列表
     * *：匹配任意数量个字符（包括 0 个字符）
     *
     * @param prefix 字符串前缀
     * @return 键的集合（全名）
     */
    public Set<String> getKeysByPrefix(final String prefix) {
        return getKeysByPattern(prefix + "*");
    }

    /**
     * key 的类型在这个方法限制为 String
     * 获得缓存的基本对象列表
     * *：匹配任意数量个字符（包括 0 个字符）
     * ?：匹配单个字符
     * []：匹配指定范围内的字符
     *
     * @param pattern 字符串格式
     * @return 键的集合（全名）
     */
    public Set<String> getKeysByPattern(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        log.info("获取 Redis 格式为 [{}] 的键 {}", pattern, keys);
        return keys;
    }

}