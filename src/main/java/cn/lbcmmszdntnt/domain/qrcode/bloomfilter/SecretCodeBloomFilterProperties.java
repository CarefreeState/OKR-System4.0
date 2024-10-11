package cn.lbcmmszdntnt.domain.qrcode.bloomfilter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-11
 * Time: 19:48
 */
@Configuration
@Setter
@Getter
@ConfigurationProperties(prefix = "spring.redisson.bloom-filter.secretcode")
public class SecretCodeBloomFilterProperties {

    private String name;

    private Long preSize;

    private Double rate;

    private Long timeout;

    private TimeUnit unit;

}
