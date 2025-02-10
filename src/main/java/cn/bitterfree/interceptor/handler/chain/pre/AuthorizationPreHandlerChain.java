package cn.bitterfree.interceptor.handler.chain.pre;

import cn.bitterfree.interceptor.handler.InterceptorHandler;
import cn.bitterfree.interceptor.handler.ext.pre.authorization.ForceRefusePreHandler;
import cn.bitterfree.interceptor.handler.ext.pre.authorization.IsIgnoreAuthorizationPreHandler;
import cn.bitterfree.interceptor.handler.ext.pre.authorization.UserTypeAuthorizationPreHandler;
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
public class AuthorizationPreHandlerChain extends InterceptorHandler implements InitializingBean {

    private final ForceRefusePreHandler forceRefusePreHandler;
    private final IsIgnoreAuthorizationPreHandler isIgnoreAuthorizationPreHandler;
    private final UserTypeAuthorizationPreHandler userTypeAuthorizationPreHandler;

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("前置授权责任链开始执行");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        InterceptorHandler.addHandlerAfter(forceRefusePreHandler, this);
        InterceptorHandler.addHandlerAfter(isIgnoreAuthorizationPreHandler, forceRefusePreHandler);
        InterceptorHandler.addHandlerAfter(userTypeAuthorizationPreHandler, isIgnoreAuthorizationPreHandler);
    }

}
