package cn.bitterfree.api.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-03-19
 * Time: 13:28
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "testdoc")
public class TestDocConfig {

    private String root;

    private String images;

}
