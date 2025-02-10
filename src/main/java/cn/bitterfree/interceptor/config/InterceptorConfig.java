package cn.bitterfree.interceptor.config;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.interceptor.GlobalInterceptor;
import cn.bitterfree.interceptor.service.UserInfoLoadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 13:19
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    private final GlobalInterceptor globalInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor)
                .addPathPatterns("/**");
    }

    @Bean
    @ConditionalOnMissingBean(UserInfoLoadService.class)
    public UserInfoLoadService userInfoLoadService() {
        return userId -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_ACCOUNT_NOT_EXIST);
        };
    }
}
