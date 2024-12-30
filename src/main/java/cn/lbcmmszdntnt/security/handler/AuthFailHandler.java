package cn.lbcmmszdntnt.security.handler;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.security.config.SecurityConfig;
import cn.lbcmmszdntnt.common.util.thread.local.ThreadLocalMapUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-22
 * Time: 2:42
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFailHandler implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        String message = String.format("%s    (%s)",
                e.getMessage(), ThreadLocalMapUtil.get(SecurityConfig.EXCEPTION_MESSAGE, String.class));
        handlerExceptionResolver.resolveException(httpServletRequest, httpServletResponse, null,
                new GlobalServiceException(message, GlobalServiceStatusCode.USER_NOT_LOGIN));
    }
}
