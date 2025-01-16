package cn.lbcmmszdntnt.domain.qrcode.generator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 23:23
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bloom-filter.binding-short-code")
public class BindingShortCodeBloomFilterProperties {

    private String name;

    private Long preSize;

    private Double rate;

    private Long timeout;

    private TimeUnit unit;

}
