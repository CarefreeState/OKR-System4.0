package cn.bitterfree.api.redis.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-13
 * Time: 21:13
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisListCache {

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisCache redisCache;

    private final RedisCacheSerializer redisCacheSerializer;

    public <E> void addAll(final String key, final List<E> list) {
        List<String> jsonList = redisCacheSerializer.toJson(list);
        log.info("存入 Redis 中的 List 缓存\t[{}]-[{}]", key, jsonList);
        redisTemplate.opsForList().rightPushAll(key, jsonList);
    }

    public <E> void addAllOver(final String key, final List<E> list) {
        redisCache.deleteObject(key);
        addAll(key, list);
    }

    public <E> void init(final String key, final List<E> list, final long timeout, final TimeUnit timeUnit) {
        addAllOver(key, list);
        redisCache.expire(key, timeout, timeUnit);
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <E> Optional<List<E>> getList(final String key, final Class<E> eClazz) {
        List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
        log.info("获取 Redis 中的 List 缓存\t[{}]-[{}]", key, jsonList);
        List<E> list = redisCacheSerializer.parse(jsonList, eClazz);
        return Optional.ofNullable(list).filter(l -> !CollectionUtils.isEmpty(l));
    }

}
