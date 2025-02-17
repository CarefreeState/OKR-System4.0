package cn.bitterfree.api.domain.media.generator;

import cn.bitterfree.api.common.util.convert.UUIDUtil;
import cn.bitterfree.api.shortcode.ShortCodeGenerator;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 23:19
 */
@Component
public class DigitalResourceCodeGenerator extends ShortCodeGenerator {

    private final DigitalResourceCodeBloomFilter bloomFilter;

    @Override
    public String getOriginCode(String baseStr, String key) {
        return baseStr + key + UUIDUtil.uuid36();
    }

    @Override
    public boolean contains(String code, String key) {
        return bloomFilter.contains(code);
    }

    @Override
    public void add(String code, String key) {
        bloomFilter.add(code);
    }

    public DigitalResourceCodeGenerator(final DigitalResourceCodeBloomFilter bloomFilter, final DigitalResourceCodeProperties shortCodeProperties) {
        super(shortCodeProperties);
        this.bloomFilter = bloomFilter;
    }

}
