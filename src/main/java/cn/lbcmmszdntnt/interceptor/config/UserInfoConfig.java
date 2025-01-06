package cn.lbcmmszdntnt.interceptor.config;

import cn.lbcmmszdntnt.interceptor.service.UserInfoLoadService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 0:04
 */
@Configuration
public class UserInfoConfig {

    @Bean
    @ConditionalOnMissingBean(UserInfoLoadService.class)
    public UserInfoLoadService userInfoLoadService() {
        return userId -> null;
    }

}
