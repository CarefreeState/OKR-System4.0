package cn.bitterfree.api.redis.bloomfilter;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;

@RequiredArgsConstructor
@Slf4j
public class RedisBloomFilter<T> {

    private final BloomFilterProperties properties;

    private final RBloomFilter<T> rBloomFilter;

    public <P> RedisBloomFilter(final RedissonClient redissonClient, final P initialData) {
        this.properties = BeanUtil.copyProperties(initialData, BloomFilterProperties.class);
        this.rBloomFilter = redissonClient.getBloomFilter(this.properties.getName());
        tryInit();
    }

    public boolean isExists() {
        // 值得注意的是布隆过滤器的位图（本体）和布隆过滤器的配置是分开来存储的
        // 本体没了才返回 false，配置在不在不影响这里，但是配置不在了的话肯定会抛异常
        return rBloomFilter.isExists();
    }

    public void tryInit() {
        rBloomFilter.tryInit(properties.getPreSize(), properties.getRate());
    }

    public void add(T key) {
        try {
            log.info("加入布隆过滤器 {}", key);
            rBloomFilter.add(key);
        } catch (RedisException e) {
            tryInit();
            rBloomFilter.add(key);
        }
    }

    public boolean contains(T key) {
        try {
            boolean contains = rBloomFilter.contains(key);
            log.info("查询布隆过滤器 {} {}", key, contains);
            return contains;
        } catch (RedisException e) {
            tryInit();
            return rBloomFilter.contains(key);
        }
    }

    public void clear() {
        log.info("清除布隆过滤器");
        rBloomFilter.delete();
        tryInit();
    }

    @Data
    public static class BloomFilterProperties {

        private String name;

        private Long preSize;

        private Double rate;

    }

}