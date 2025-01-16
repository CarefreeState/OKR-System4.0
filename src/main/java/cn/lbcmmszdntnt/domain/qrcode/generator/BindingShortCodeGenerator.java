package cn.lbcmmszdntnt.domain.qrcode.generator;

import cn.lbcmmszdntnt.shortcode.ShortCodeGenerator;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 23:19
 */
@Component
public class BindingShortCodeGenerator extends ShortCodeGenerator {

    public BindingShortCodeGenerator(final BindingShortCodeBloomFilter bloomFilter, final BindingShortCodeProperties shortCodeProperties) {
        super(bloomFilter, shortCodeProperties);
    }

}
