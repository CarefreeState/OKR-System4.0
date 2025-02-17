package cn.bitterfree.api.redis.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Set;
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
public class RedisSetCache {

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisCache redisCache;

    private final RedisCacheSerializer redisCacheSerializer;

    public <E> void addAll(final String key, final Set<E> set) {
        Set<String> jsonSet = redisCacheSerializer.toJson(set);
        log.info("存入 Redis 中的 Set 缓存\t[{}]-[{}]", key, jsonSet);
        String[] jsonArr = jsonSet.toArray(new String[0]);
        redisTemplate.opsForSet().add(key, jsonArr);
    }

    public <E> void addAllOver(final String key, final Set<E> set) {
        redisCache.deleteObject(key);
        addAll(key, set);
    }

    public <E> void init(final String key, final Set<E> set, final long timeout, final TimeUnit timeUnit) {
        addAllOver(key, set);
        redisCache.expire(key, timeout, timeUnit);
    }

    public <E> Optional<Set<E>> getSet(final String key, final Class<E> eClazz) {
        Set<String> jsonSet = redisTemplate.opsForSet().members(key);
        log.info("获取 Redis 中的 Set 缓存\t[{}]-[{}]", key, jsonSet);
        Set<E> set = redisCacheSerializer.parse(jsonSet, eClazz);
        return Optional.ofNullable(set).filter(s -> !CollectionUtils.isEmpty(s));
    }

    public <E> Boolean contains(final String key, final E e) {
        String jsonE = redisCacheSerializer.toJson(key);
        Boolean flag = getSet(key, e.getClass()).map(set -> set.contains(jsonE)).orElse(Boolean.FALSE);
        log.info("查询 Redis 的 Set 的值是否存在\t[{}.{}]-[{}]", key, jsonE, flag);
        return flag;
    }

    public <E> void remove(final String key, final E e) {
        String jsonE = redisCacheSerializer.toJson(e);
        log.info("删除 Redis 中的 Set 的值\tkey[{}.{}]", key, jsonE);
        redisTemplate.opsForSet().remove(key, jsonE);
    }

}
