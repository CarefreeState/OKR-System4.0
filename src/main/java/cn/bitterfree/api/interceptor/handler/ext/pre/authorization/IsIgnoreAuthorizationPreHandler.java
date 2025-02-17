package cn.bitterfree.api.interceptor.handler.ext.pre.authorization;

import cn.bitterfree.api.interceptor.context.InterceptorContext;
import cn.bitterfree.api.interceptor.handler.InterceptorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 23:20
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IsIgnoreAuthorizationPreHandler extends InterceptorHandler {

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 判断是不是可以直接忽略的请求
        if(Boolean.FALSE.equals(InterceptorContext.getInterceptProperties().getAuthorize())) {
            log.warn("忽略授权");
            InterceptorContext.setIsAuthorized(Boolean.TRUE);
        }
    }
}
