package cn.lbcmmszdntnt.interceptor.handler.ext.pre.init;

import cn.lbcmmszdntnt.common.util.web.HttpUtil;
import cn.lbcmmszdntnt.interceptor.config.CustomInterceptConfig;
import cn.lbcmmszdntnt.interceptor.config.properties.CustomInterceptProperties;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
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
            if(HttpUtil.anyMatchPath(intercept.getUrls(), requestURI)) {
                // 找第一个匹配的参数设置到线程变量里
                InterceptorContext.setInterceptProperties(intercept.getProperties());
                break;
            }
        }
    }
}
