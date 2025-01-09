package cn.lbcmmszdntnt.interceptor.handler.ext.pre.init;

import cn.hutool.core.util.IdUtil;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 23:17
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LogInitPreHandler extends InterceptorHandler {

    @Value("${spring.trace-id}")
    private String traceId;

    @Value("${spring.trace-uri}")
    private String uri;

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = IdUtil.objectId();
        MDC.put(traceId, requestId);
        MDC.put(uri, request.getRequestURI());
        response.setHeader(traceId, requestId);
    }
}
