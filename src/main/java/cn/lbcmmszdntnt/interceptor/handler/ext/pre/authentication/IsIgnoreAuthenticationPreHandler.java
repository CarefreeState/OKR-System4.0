package cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication;

import cn.lbcmmszdntnt.common.util.web.HttpUtil;
import cn.lbcmmszdntnt.interceptor.config.IgnoreUrlsConfig;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 20:38
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IsIgnoreAuthenticationPreHandler extends InterceptorHandler {

    private final IgnoreUrlsConfig ignoreUrlsConfig;

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 判断是不是可以直接忽略的请求
        String requestURI = request.getRequestURI();
        boolean anyMatchPath = HttpUtil.anyMatchPath(ignoreUrlsConfig.getUrls(), requestURI);
        if (Boolean.FALSE.equals(anyMatchPath)) {
            Optional.ofNullable(InterceptorContext.getIntercept()).ifPresent(intercept -> {
                if (intercept.authenticate()) {
                    InterceptorContext.setIsAuthenticated(Boolean.TRUE);
                }
            });
        } else {
            InterceptorContext.setIsAuthenticated(Boolean.TRUE);
        }
    }
}
