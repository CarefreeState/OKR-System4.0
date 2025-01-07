package cn.lbcmmszdntnt.interceptor.handler.ext.pre.authentication;

import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import cn.lbcmmszdntnt.interceptor.service.UserInfoLoadService;
import cn.lbcmmszdntnt.jwt.JwtUtil;
import cn.lbcmmszdntnt.jwt.TokenVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 20:37
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHeaderAuthenticationPreHandler extends InterceptorHandler {

    private final UserInfoLoadService userInfoLoadService;

    @Override
    public Boolean condition() {
        return !InterceptorContext.isAuthenticated();
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            TokenVO tokenVO = JwtUtil.parseJwtFromHeader(request, response);
            User user = userInfoLoadService.loadUser(tokenVO.getUserId());
            InterceptorContext.setUser(user);
            InterceptorContext.setIsAuthenticated(Boolean.TRUE);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
