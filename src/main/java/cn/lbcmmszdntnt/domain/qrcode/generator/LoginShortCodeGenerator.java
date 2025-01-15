package cn.lbcmmszdntnt.domain.qrcode.generator;

import cn.lbcmmszdntnt.shortcode.ShortCodeGenerator;
import cn.lbcmmszdntnt.domain.qrcode.config.LoginShortCodeProperties;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 18:59
 */
@Component
public class LoginShortCodeGenerator extends ShortCodeGenerator {

    public LoginShortCodeGenerator(final LoginSecretCodeBloomFilter bloomFilter, final LoginShortCodeProperties shortCodeProperties) {
        super(bloomFilter, shortCodeProperties);
    }
}
