package cn.lbcmmszdntnt.interceptor.config;

import cn.lbcmmszdntnt.interceptor.config.properties.CustomInterceptProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 14:59
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "secure.custom")
public class CustomInterceptConfig {

    private List<CustomInterceptProperties> list;



}
