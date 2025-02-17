package cn.bitterfree.api.shortcode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 18:53
 */
@Component
@Slf4j
public class NormalShortCodeGenerator extends ShortCodeGenerator {

    public NormalShortCodeGenerator(final NormalShortCodeProperties shortCodeProperties) {
        super(shortCodeProperties);
    }

}
