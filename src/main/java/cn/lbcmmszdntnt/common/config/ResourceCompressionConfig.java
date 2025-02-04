package cn.lbcmmszdntnt.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 21:33
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "resource.compression")
public class ResourceCompressionConfig {

    private Integer threshold;

}
