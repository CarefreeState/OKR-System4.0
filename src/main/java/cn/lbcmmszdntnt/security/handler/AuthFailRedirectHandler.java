package cn.lbcmmszdntnt.security.handler;

import cn.lbcmmszdntnt.security.config.SecurityConfig;
import cn.lbcmmszdntnt.util.thread.local.ThreadLocalMapUtil;
import cn.lbcmmszdntnt.util.web.HttpUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-19
 * Time: 11:52
 */
@Component
@Slf4j
public class AuthFailRedirectHandler implements AuthenticationEntryPoint {

    public final static String REDIRECT_URL = "/unlisted";

    @Value("${spring.domain}")
    private String domain;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        String message = String.format("%s    (%s)",
                e.getMessage(), ThreadLocalMapUtil.get(SecurityConfig.EXCEPTION_MESSAGE, String.class));
        String requestURI = httpServletRequest.getRequestURI();
        String redirect = domain + REDIRECT_URL + HttpUtil.getQueryString(new HashMap<>(){{
            this.put(SecurityConfig.EXCEPTION_MESSAGE, message);
        }});
        log.warn("'{}' 重定向 --> '{}'", requestURI, redirect);
        httpServletResponse.setStatus(HttpStatus.FOUND.value());
        httpServletResponse.setHeader("Location", redirect);
    }
}
