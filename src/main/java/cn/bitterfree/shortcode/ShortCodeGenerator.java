package cn.bitterfree.shortcode;

import cn.bitterfree.common.util.convert.ShortCodeUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 18:08
 */
public class ShortCodeGenerator {

    private final static Integer DEFAULT_LENGTH = 6;

    private final int length;
    private final String key;

    public <P> ShortCodeGenerator(P properties) {
        ShortCodeProperties shortCodeProperties = BeanUtil.copyProperties(properties, ShortCodeProperties.class);
        length = Optional.ofNullable(shortCodeProperties.getLength()).filter(l -> l.compareTo(0) > 0).orElse(DEFAULT_LENGTH);
        key = Optional.ofNullable(shortCodeProperties.getKey()).filter(StringUtils::hasText).orElse("");
    }

    public String getOriginCode(String baseStr, String key) {
        return baseStr + key;
    };

    public boolean contains(String code, String key) {
        return Boolean.FALSE;
    }

    public void add(String code, String key) {

    };

    public final String generate(String baseStr) {
        // length 为 null 或者小于 0 会走默认值，默认长度为 6， 长度范围必须在 [1, 29] 以内，否则生成不了
        String code = baseStr;
        do {
            code = ShortCodeUtil.subCodeByString(getOriginCode(code, key), length);
        } while (contains(code, key));
        add(code, key);
        return code;
    }

    public final String generate() {
        return generate("");
    }

    @Data
    public static class ShortCodeProperties {

        private String key;

        private Integer length;

    }

}
