package cn.bitterfree.api.common.cors.filter;

import cn.bitterfree.api.common.util.convert.ObjectUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.cors")
public class CorsFilter implements Filter {

    private String allowOrigin;

    private Set<String> allowOriginSet;

    private String allowMethods;

    private String maxAge;

    private String allowHeaders;

    private String allowCredentials;

    @PostConstruct
    public void init() {
        allowOriginSet = ObjectUtil.split(allowOrigin, ",");
        log.info("允许跨域的请求源：{}", allowOriginSet);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 获取请求源
        String method = httpRequest.getMethod();
        String origin = httpRequest.getHeader(HttpHeaders.ORIGIN);
        log.info("来自 {} 的 {} 请求", origin, method);
        if(allowOriginSet.contains("*")) {
            log.info("允许源 *");
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        }
        if(StringUtils.hasText(origin) && allowOrigin.contains(origin)) {
            log.info("允许源 {}", origin);
            // 可以设置允许访问的域，也可以是具体的域名
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        }
        log.info("设置响应头 allowMethods {} maxAge {} allowHeaders {} allowCredentials {}",
                allowMethods, maxAge, allowHeaders, allowCredentials);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowMethods);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, maxAge);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, allowCredentials);
        // 处理 OPTIONS 请求
        if(HttpMethod.OPTIONS.name().equals(method)) {
            httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            httpResponse.setHeader(HttpHeaders.CONTENT_LENGTH, "0");
            httpResponse.setStatus(HttpStatus.NO_CONTENT.value());
        }
        chain.doFilter(request, response);
    }

}