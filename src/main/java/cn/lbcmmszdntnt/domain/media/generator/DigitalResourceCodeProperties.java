package cn.lbcmmszdntnt.domain.media.generator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 23:21
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "short-code-generator.digital-resource")
public class DigitalResourceCodeProperties {

    private String key;

    private Integer length;

}
