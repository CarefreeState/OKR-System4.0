package cn.lbcmmszdntnt.domain.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 23:27
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "quadrant-cycle.limit")
public class QuadrantCycleConfig {

    private Integer second;

    private Integer multiple;

}
