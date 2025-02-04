package cn.lbcmmszdntnt.filter;

import cn.lbcmmszdntnt.sse.annotation.SseRequest;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 15:33
 */
@Component
@Slf4j
@RequiredArgsConstructor
//@WebFilter(urlPatterns = "/sse/**") // 也可以约定 sse 请求必须有 /sse 前缀
// 如果没有这个类，就需要其他的请求代理服务群设置这些响应头部，不然 https 的 sse 请求没法建立连接
public class SseFilter implements Filter {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Optional.ofNullable(requestMappingHandlerMapping.getHandler((HttpServletRequest) request))
                    .map(HandlerExecutionChain::getHandler)
                    .filter(HandlerMethod.class::isInstance)
                    .map(HandlerMethod.class::cast)
                    .map(HandlerMethod::getMethod)
                    .filter(method -> method.isAnnotationPresent(SseRequest.class))
                    .ifPresent(method -> {
                        HttpServletResponse httpResponse = (HttpServletResponse) response;
                        httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, "text/event-stream");
                        httpResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
                        httpResponse.setHeader(HttpHeaders.CONNECTION, "keep-alive");
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            chain.doFilter(request, response);
        }
    }
}
