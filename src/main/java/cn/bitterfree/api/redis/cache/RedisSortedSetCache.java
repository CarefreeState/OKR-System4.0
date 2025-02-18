package cn.bitterfree.api.redis.cache;

import cn.bitterfree.api.common.util.convert.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-18
 * Time: 0:54
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisSortedSetCache {

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisCache redisCache;

    private final RedisCacheSerializer redisCacheSerializer;

    public <E> void add(final String key, final E value, final double score) {
        String jsonValue = redisCacheSerializer.toJson(value);
        log.info("存入 Redis 中的 SortedSet 缓存\t[{}]-[{}, {}]", key, jsonValue, score);
        redisTemplate.opsForZSet().add(key, jsonValue, score);
    }

    public <E> void add(final String key, final E value, final double score, final long timeout, final TimeUnit timeUnit) {
        redisCache.deleteObject(key);
        add(key, value, score);
        redisCache.expire(key, timeout, timeUnit);
    }

    public <E> Set<E> popMax(final String key, final Class<E> eClazz, final long count) {
        Set<E> popSet = ObjectUtil.nonNullstream(redisTemplate.opsForZSet().popMax(key, count)) // 弹出的数量 <= count
                .map(ZSetOperations.TypedTuple::getValue)
                .map(value -> redisCacheSerializer.parse(value, eClazz))
                .collect(Collectors.toSet());
        log.info("Redis 中的 SortedSet {} pop {}", key, popSet);
        return popSet;
    }

    public <E> Set<E> popMin(final String key, final Class<E> eClazz, final long count) {
        Set<E> popSet = ObjectUtil.nonNullstream(redisTemplate.opsForZSet().popMin(key, count)) // 弹出的数量 <= count
                .map(ZSetOperations.TypedTuple::getValue)
                .map(value -> redisCacheSerializer.parse(value, eClazz))
                .collect(Collectors.toSet());
        log.info("Redis 中的 SortedSet {} pop {}", key, popSet);
        return popSet;
    }

    public <E> Set<E> rangeByScore(final String key, final Class<E> eClazz, final long min, final long max) {
        Set<E> rangeSet = ObjectUtil.nonNullstream(redisTemplate.opsForZSet().rangeByScore(key, min, max))
                .map(value -> redisCacheSerializer.parse(value, eClazz))
                .collect(Collectors.toSet());
        log.info("Redis 中的 SortedSet {} range [{}, {}] {}", key, min, max, rangeSet);
        return rangeSet;
    }

    public <E> void removeRangeByScore(final String key, final long min, final long max) {
        redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
        log.info("Redis 中的 SortedSet {} remove [{}, {}]", key, min, max);
    }

    public Long size(String key) {
        Long size = redisTemplate.opsForZSet().zCard(key);
        log.info("Redis 中的 SortedSet {} 长度 {}", key, size);
        return size;
    }

}
