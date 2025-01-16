package cn.lbcmmszdntnt.domain.qrcode.generator;

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
public class LoginShortCodeBloomFilter extends RedisBloomFilter<String> {

    // todo 登录 secret 其实只有 五分钟的有效日期，怎么解决伪数据的存在
    public LoginShortCodeBloomFilter(final RedissonClient redissonClient, final LoginShortCodeBloomFilterProperties loginShortCodeBloomFilterProperties) {
        super(redissonClient, loginShortCodeBloomFilterProperties);
    }

}
