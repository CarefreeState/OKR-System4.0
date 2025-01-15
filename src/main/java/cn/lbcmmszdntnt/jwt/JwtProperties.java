package cn.lbcmmszdntnt.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-05
 * Time: 21:35
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.jwt")
public class JwtProperties {

    /**
     * 用户端用户生成jwt令牌相关配置
     */
    private String secretKey;

    private String applicationName;

    private Long ttl;

    private Long refreshTime;

    private TimeUnit unit;

    private String tokenName;

    private String customKey;

}
