package cn.lbcmmszdntnt.domain.qrcode.generator;

import cn.lbcmmszdntnt.redis.bloomfilter.RedisBloomFilter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 23:23
 */
@Repository
@Slf4j
public class BindingShortCodeBloomFilter extends RedisBloomFilter<String> {

    public BindingShortCodeBloomFilter(final RedissonClient redissonClient, final BindingShortCodeBloomFilterProperties bindingShortCodeBloomFilterProperties) {
        super(redissonClient, bindingShortCodeBloomFilterProperties);
    }

}
