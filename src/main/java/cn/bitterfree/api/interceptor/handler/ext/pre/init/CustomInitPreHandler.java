package cn.bitterfree.api.interceptor.handler.ext.pre.init;

import cn.bitterfree.api.common.util.web.HttpRequestUtil;
import cn.bitterfree.api.interceptor.config.CustomInterceptConfig;
import cn.bitterfree.api.interceptor.config.CustomInterceptProperties;
import cn.bitterfree.api.interceptor.config.InterceptProperties;
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
 * Date: 2025-01-09
 * Time: 14:57
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomInitPreHandler extends InterceptorHandler {

    private final CustomInterceptConfig customInterceptConfig;

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        for(CustomInterceptProperties intercept : customInterceptConfig.getList()) {
            if(HttpRequestUtil.anyMatchPath(intercept.getUrls(), requestURI)) {
                // 找第一个匹配的参数设置到线程变量里
                InterceptProperties properties = intercept.getProperties();
                log.info("自定义请求认证鉴权：{}", properties);
                InterceptorContext.setInterceptProperties(properties);
                break;
            }
        }
    }
}
