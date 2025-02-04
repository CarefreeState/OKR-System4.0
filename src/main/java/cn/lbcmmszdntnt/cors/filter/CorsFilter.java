package cn.lbcmmszdntnt.cors.filter;

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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        allowOriginSet = Optional.ofNullable(allowOrigin)
                .stream()
                .filter(StringUtils::hasText)
                .map(origins -> origins.split(","))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        log.info("允许跨域的请求源：{}", allowOriginSet);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 获取请求源
        String origin = httpRequest.getHeader(HttpHeaders.ORIGIN);
        if(allowOriginSet.contains("*")) {
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        }
        if(StringUtils.hasText(origin) && allowOrigin.contains(origin)) {
            // 可以设置允许访问的域，也可以是具体的域名
            httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        }
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, allowMethods);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, maxAge);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
        httpResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, allowCredentials);
        chain.doFilter(request, response);
    }

}