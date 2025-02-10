package cn.bitterfree.interceptor.handler.ext.pre.authentication;

import cn.bitterfree.interceptor.context.InterceptorContext;
import cn.bitterfree.interceptor.handler.InterceptorHandler;
import cn.bitterfree.interceptor.jwt.TokenVO;
import cn.bitterfree.interceptor.service.UserInfoLoadService;
import cn.bitterfree.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 20:38
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationPreHandler extends InterceptorHandler {

    private final UserInfoLoadService userInfoLoadService;

    @Override
    public Boolean condition() {
        return !InterceptorContext.isAuthenticated();
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            Optional.ofNullable(InterceptorContext.getJwt())
                    .filter(StringUtils::hasText)
                    .map(token -> {
                        log.info("当前请求访问令牌 {}", token);
                        return JwtUtil.parseJwtData(token, new TokenVO(), response);
                    })
                    .map(TokenVO::getUserId)
                    .map(userInfoLoadService::loadUser)
                    .ifPresent(user -> {
                        log.info("当前用户认证成功 {}", user);
                        InterceptorContext.setUser(user);
                        InterceptorContext.setIsAuthenticated(Boolean.TRUE);
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
