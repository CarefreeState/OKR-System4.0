package cn.lbcmmszdntnt.interceptor;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.chain.after.AfterHandlerChain;
import cn.lbcmmszdntnt.interceptor.handler.chain.pre.AuthenticationPreHandlerChain;
import cn.lbcmmszdntnt.interceptor.handler.chain.pre.AuthorizationPreHandlerChain;
import cn.lbcmmszdntnt.interceptor.handler.chain.pre.InitPreHandlerChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

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

    private final InitPreHandlerChain initPreHandlerChain;
    private final AuthenticationPreHandlerChain authenticationPreHandlerChain;
    private final AuthorizationPreHandlerChain authorizationPreHandlerChain;
    private final AfterHandlerChain afterHandlerChain;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 执行初始化认拦截参数 pre 链
        initPreHandlerChain.handle(request, response, handler);
        // 若没有拦截参数，则无法访问接口（这种情况的出现，代表这个接口并没有在配置文件里自定义配置，也没有目标方法的 Intercept 注解）
        Optional.ofNullable(InterceptorContext.getInterceptProperties()).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.SYSTEM_API_VISIT_FAIL));
        // 执行认证 pre 链
        authenticationPreHandlerChain.handle(request, response, handler);
        // 判断是否认证成功
        if(!InterceptorContext.isAuthenticated()) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_AUTHENTICATED);
        }
        // 执行授权 pre 链
        authorizationPreHandlerChain.handle(request, response, handler);
        // 判断是否授权成功
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
