package cn.bitterfree.api.interceptor.handler.ext.pre.init;

import cn.bitterfree.api.interceptor.annotation.InterceptHelper;
import cn.bitterfree.api.interceptor.config.InterceptProperties;
import cn.bitterfree.api.interceptor.context.InterceptorContext;
import cn.bitterfree.api.interceptor.handler.InterceptorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 14:36
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HandlerMethodInitPreHandler extends InterceptorHandler {


    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取 Intercept 注解
        if(handler instanceof HandlerMethod handlerMethod) {
            // 值得注意的是，websocket 和 资源访问（/api/xxx.png 之类的） 的请求，handler 的实现并不是 HandlerMethod
            Optional.ofNullable(InterceptHelper.getIntercept(handlerMethod.getMethod())).ifPresent(intercept -> {
                log.info("目标方法注解认证鉴权：{}", intercept);
                InterceptProperties interceptProperties = InterceptProperties.builder()
                        .permit(List.of(intercept.permit()))
                        .authenticate(intercept.authenticate())
                        .authorize(intercept.authorize())
                        .build();
                InterceptorContext.setInterceptProperties(interceptProperties);
            });
        }
    }
}
