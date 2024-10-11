package cn.lbcmmszdntnt.domain.qrcode.bloomfilter;

import cn.lbcmmszdntnt.domain.qrcode.model.convert.SecretCodeConverter;
import cn.lbcmmszdntnt.redis.bloomfilter.BloomFilterProperties;
import cn.lbcmmszdntnt.redis.bloomfilter.RedisBloomFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
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
@RequiredArgsConstructor
public class SecretCodeBloomFilter extends RedisBloomFilter<String> implements InitializingBean {

    private final RedissonClient redissonClient;

    private final SecretCodeBloomFilterProperties secretCodeBloomFilterProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 获取配置
        BloomFilterProperties bloomFilterProperties = SecretCodeConverter.INSTANCE.secretCodeBloomFilterPropertiesToBloomFilterProperties(secretCodeBloomFilterProperties);
        // 初始化布隆过滤器
        initFilter(redissonClient, bloomFilterProperties);
    }
}
