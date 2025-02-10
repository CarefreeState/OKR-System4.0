package cn.bitterfree.domain.media.generator;

import cn.bitterfree.redis.bloomfilter.RedisBloomFilter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 19:47
 */
@Repository
@Slf4j
public class DigitalResourceCodeBloomFilter extends RedisBloomFilter<String> {

    public DigitalResourceCodeBloomFilter(final RedissonClient redissonClient, final DigitalResourceCodeBloomFilterProperties digitalResourceCodeBloomFilterProperties) {
        super(redissonClient, digitalResourceCodeBloomFilterProperties);
    }

}
