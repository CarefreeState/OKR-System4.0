package cn.lbcmmszdntnt.domain.user.interceptor;

import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication.JwtAuthenticationPreHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 12:15
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtIsInBlacklistInterceptorConfig implements InitializingBean {

    private final JwtIsInBlacklistPreHandler jwtIsInBlacklistPreHandler;
    private final JwtAuthenticationPreHandler jwtAuthenticationPreHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        InterceptorHandler.addHandlerAfter(jwtIsInBlacklistPreHandler, jwtAuthenticationPreHandler);
    }
}
