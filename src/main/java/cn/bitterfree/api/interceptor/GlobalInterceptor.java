package cn.bitterfree.api.interceptor;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.interceptor.context.InterceptorContext;
import cn.bitterfree.api.interceptor.handler.chain.after.AfterHandlerChain;
import cn.bitterfree.api.interceptor.handler.chain.pre.AuthenticationPreHandlerChain;
import cn.bitterfree.api.interceptor.handler.chain.pre.AuthorizationPreHandlerChain;
import cn.bitterfree.api.interceptor.handler.chain.pre.InitPreHandlerChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
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

    public void preHandleChain(HttpServletRequest request, HttpServletResponse response, Object handler) {
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
    }

    public void afterHandleChain(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 执行 after 链
        afterHandlerChain.handle(request, response, handler);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 忽略 OPTIONS 请求
        if(HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            return Boolean.TRUE;
        }
        try {
            preHandleChain(request, response, handler);
        } catch (Throwable t) {
            log.error("前置链执行失败 {} ", t.getMessage());
            // 如果抛异常，则不会触发后置方法了，这里需要手动执行后置链！不能放 finally 里面！
            afterHandleChain(request, response, handler);
            throw  t;
        }
        // 上述操作若无抛出异常则认为认证授权通过
        log.info("允许本次请求访问此接口");
        return Boolean.TRUE;
    }

    @Override
    /**
     * 这个方法是有执行条件的，必须 preHandle 方法执行完毕并返回 true
     * 所以平时在
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 执行 after 链
        afterHandleChain(request, response, handler);
    }
}
