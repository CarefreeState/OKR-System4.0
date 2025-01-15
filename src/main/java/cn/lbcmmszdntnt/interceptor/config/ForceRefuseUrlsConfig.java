package cn.lbcmmszdntnt.interceptor.config;

import cn.lbcmmszdntnt.common.util.convert.ObjectUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 13:15
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "secure.refused")
public class ForceRefuseUrlsConfig {

    private List<String> urls;

    @PostConstruct
    public void init() {
        urls = ObjectUtil.distinctNonNullStream(urls).toList();
    }

}
