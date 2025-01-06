package cn.lbcmmszdntnt.interceptor.handler.ext.pre.authorization;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 20:48
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ForceRefusePreHandler extends InterceptorHandler {

    public final static String[] SWAGGERS = {
            "/doc.html/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/error",
            "/favicon.ico",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html/**",
    };

    @Value("${visit.swagger}")
    private Boolean swaggerCanBeVisited;

    private final static List<String> DEFAULT_FORCE_REFUSE_PATH_PATTERNS = List.of(SWAGGERS);

    @Override
    public List<String> pathPatterns() {
        return Boolean.TRUE.equals(swaggerCanBeVisited) ? List.of() : DEFAULT_FORCE_REFUSE_PATH_PATTERNS;
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.error("强制拦截 {}", request.getRequestURI());
        throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_AUTHORIZED);
    }
}
