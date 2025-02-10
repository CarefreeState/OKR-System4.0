package cn.bitterfree.domain.auth.generator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 18:58
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "short-code-generator.login")
public class LoginShortCodeProperties {

    private String key;

    private Integer length;

}
