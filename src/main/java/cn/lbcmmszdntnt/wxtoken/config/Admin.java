package cn.lbcmmszdntnt.wxtoken.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 19:11
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.wx")
public class Admin {

    private String appid;

    private String secret;

}
