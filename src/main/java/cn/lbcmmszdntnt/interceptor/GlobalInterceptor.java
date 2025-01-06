package cn.lbcmmszdntnt.interceptor;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.annotation.InterceptHelper;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.chain.after.AfterHandlerChain;
import cn.lbcmmszdntnt.interceptor.handler.chain.pre.AuthenticationPreHandlerChain;
import cn.lbcmmszdntnt.interceptor.handler.chain.pre.AuthorizationPreHandlerChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 19:31
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalInterceptor implements HandlerInterceptor {

    private final AuthenticationPreHandlerChain authenticationPreHandlerChain;
    private final AuthorizationPreHandlerChain authorizationPreHandlerChain;
    private final AfterHandlerChain afterHandlerChain;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 Intercept 注解
        if(handler instanceof HandlerMethod handlerMethod) {
            InterceptorContext.setIntercept(InterceptHelper.getIntercept(handlerMethod.getMethod()));
        }
        // 执行认证 pre 链
        authenticationPreHandlerChain.handle(request, response, handler);
        if(!InterceptorContext.isAuthenticated()) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_AUTHENTICATED);
        }
        // 执行授权 pre 链
        authorizationPreHandlerChain.handle(request, response, handler);
        if(!InterceptorContext.isAuthorized()) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_AUTHORIZED);
        }
        // 上述操作若无抛出异常则认为认证授权通过
        return Boolean.TRUE;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 执行 after 链
        afterHandlerChain.handle(request, response, handler);
    }
}
