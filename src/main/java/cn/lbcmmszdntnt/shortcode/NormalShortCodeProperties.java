package cn.lbcmmszdntnt.shortcode;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 18:54
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "short-code-generator.normal")
public class NormalShortCodeProperties {

    private String key;

    private Integer length;

    private Boolean unique;

}
