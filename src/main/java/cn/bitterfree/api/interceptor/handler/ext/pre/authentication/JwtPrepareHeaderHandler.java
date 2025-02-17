package cn.bitterfree.api.interceptor.handler.ext.pre.authentication;

import cn.bitterfree.api.interceptor.context.InterceptorContext;
import cn.bitterfree.api.interceptor.handler.InterceptorHandler;
import cn.bitterfree.api.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 13:53
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtPrepareHeaderHandler extends InterceptorHandler {

    @Override
    public Boolean condition() {
        return !InterceptorContext.isAuthenticated() && !StringUtils.hasText(InterceptorContext.getJwt());
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String jwt = JwtUtil.getJwtFromHeader(request);
        InterceptorContext.setJwt(jwt);
    }
}
