package cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication;

import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import cn.lbcmmszdntnt.interceptor.jwt.TokenVO;
import cn.lbcmmszdntnt.interceptor.service.UserInfoLoadService;
import cn.lbcmmszdntnt.jwt.JwtUtil;
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
                    .map(token -> JwtUtil.parseJwtData(token, new TokenVO(), response))
                    .map(TokenVO::getUserId)
                    .map(userInfoLoadService::loadUser)
                    .ifPresent(user -> {
                        InterceptorContext.setUser(user);
                        InterceptorContext.setIsAuthenticated(Boolean.TRUE);
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
