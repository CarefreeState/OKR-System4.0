package cn.lbcmmszdntnt.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-05
 * Time: 21:35
 */
@Component
@ConfigurationProperties(prefix = "okr.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * 用户端用户生成jwt令牌相关配置
     */
    private String secretKey;

    private Long ttl;

    private Long refreshTime;

    private TimeUnit unit;

    private String tokenName;

}
