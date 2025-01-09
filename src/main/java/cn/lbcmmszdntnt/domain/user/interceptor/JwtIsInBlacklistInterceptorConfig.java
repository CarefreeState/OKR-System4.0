package cn.lbcmmszdntnt.domain.user.interceptor;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication.JwtAuthenticationPreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
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
