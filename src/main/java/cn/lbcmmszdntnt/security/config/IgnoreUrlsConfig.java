package cn.lbcmmszdntnt.security.config;

import cn.lbcmmszdntnt.common.util.convert.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SpringSecurity 白名单资源路径配置
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "secure.ignored")
public class IgnoreUrlsConfig implements InitializingBean {

    private List<String> urls;

    @Override
    public void afterPropertiesSet() {
        urls = ObjectUtil.distinctNonNullStream(urls).toList();
    }
}
