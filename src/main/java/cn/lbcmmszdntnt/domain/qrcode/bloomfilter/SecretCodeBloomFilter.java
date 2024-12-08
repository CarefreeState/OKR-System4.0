package cn.lbcmmszdntnt.domain.qrcode.bloomfilter;

import cn.lbcmmszdntnt.redis.bloomfilter.RedisBloomFilter;
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
public class SecretCodeBloomFilter extends RedisBloomFilter<String> {

    public SecretCodeBloomFilter(final RedissonClient redissonClient, final SecretCodeBloomFilterProperties secretCodeBloomFilterProperties) {
        super(redissonClient, secretCodeBloomFilterProperties);
    }

}
