package cn.lbcmmszdntnt.shortcode;

import cn.hutool.core.bean.BeanUtil;
import cn.lbcmmszdntnt.common.util.convert.ShortCodeUtil;
import cn.lbcmmszdntnt.common.util.convert.UUIDUtil;
import cn.lbcmmszdntnt.redis.bloomfilter.RedisBloomFilter;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 18:08
 */
public class ShortCodeGenerator {

    public static final String DEFAULT_BASE_STR = "";
    public static final int DEFAULT_LENGTH = 6;

    private final RedisBloomFilter<String> bloomFilter;

    private final ShortCodeProperties shortCodeProperties;

    public <P> ShortCodeGenerator(RedisBloomFilter<String> bloomFilter, P shortCodeProperties) {
        this.bloomFilter = bloomFilter;
        this.shortCodeProperties = BeanUtil.copyProperties(shortCodeProperties, ShortCodeProperties.class);
    }

    public String convert(String baseStr) {
        int length = Optional.ofNullable(shortCodeProperties.getLength()).filter(l -> l.compareTo(0) > 0).orElse(DEFAULT_LENGTH);
        if(Boolean.TRUE.equals(shortCodeProperties.getUnique())) {
            // unique == true
            String code = baseStr;
            do {
                code = ShortCodeUtil.subCodeByString(code + UUIDUtil.uuid32(), length);
            } while (bloomFilter.contains(code));
            bloomFilter.add(code);
            return code;
        } else {
            // unique == null/false
            String key = shortCodeProperties.getKey();
            return ShortCodeUtil.subCodeByString(baseStr + key, length);
        }
    }

    public String convert() {
        return convert(DEFAULT_BASE_STR);
    }

}
