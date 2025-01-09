package cn.lbcmmszdntnt.interceptor.handler.chain.pre;

import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication.IsIgnoreAuthenticationPreHandler;
import cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication.JwtAuthenticationPreHandler;
import cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication.JwtPrepareHeaderHandler;
import cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication.JwtPrepareParameterHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 20:55
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationPreHandlerChain extends InterceptorHandler implements InitializingBean {

    private final IsIgnoreAuthenticationPreHandler isIgnoreAuthenticationPreHandler;
    private final JwtPrepareHeaderHandler jwtPrepareHeaderHandler;
    private final JwtPrepareParameterHandler jwtPrepareParameterHandler;
    private final JwtAuthenticationPreHandler jwtAuthenticationPreHandler;

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("前置认证责任链开始执行");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        InterceptorHandler.addHandlerAfter(isIgnoreAuthenticationPreHandler, this);
        InterceptorHandler.addHandlerAfter(jwtPrepareHeaderHandler, isIgnoreAuthenticationPreHandler);
        InterceptorHandler.addHandlerAfter(jwtPrepareParameterHandler, jwtPrepareHeaderHandler);
        InterceptorHandler.addHandlerAfter(jwtAuthenticationPreHandler, jwtPrepareParameterHandler);
    }

}
