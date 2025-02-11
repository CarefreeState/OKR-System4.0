package cn.bitterfree.sse.filter;

import cn.bitterfree.sse.annotation.SseRequestHelper;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 15:33
 */
@Slf4j
@Getter
@Setter
@Configuration
//@WebFilter(urlPatterns = "/sse/**") // 也可以约定 sse 请求必须有 /sse 前缀
// 如果没有这个类，就需要其他的请求代理服务群设置这些响应头部，不然 https 的 sse 请求没法建立连接
@ConfigurationProperties(prefix = "okr.sse")
public class SseFilter implements Filter {

    private String contentType;

    private String cacheControl;

    private String connection;

    private String pragma;

    private Long expires;

    private Map<String, String> otherHeaders;

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Optional.ofNullable(requestMappingHandlerMapping.getHandler((HttpServletRequest) request))
                    .map(HandlerExecutionChain::getHandler)
                    .filter(SseRequestHelper::isSseRequest)
                    .ifPresent(handler -> {
                        HttpServletResponse httpResponse = (HttpServletResponse) response;
                        httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
                        httpResponse.setHeader(HttpHeaders.CACHE_CONTROL, cacheControl);
                        httpResponse.setHeader(HttpHeaders.CONNECTION, connection);
                        httpResponse.setHeader(HttpHeaders.PRAGMA, pragma);
                        httpResponse.setDateHeader(HttpHeaders.EXPIRES, expires);
                        otherHeaders.forEach(httpResponse::setHeader);
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            chain.doFilter(request, response);
        }
    }
}
