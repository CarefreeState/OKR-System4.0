package cn.lbcmmszdntnt.security.filter;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.model.dto.detail.LoginUser;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.security.config.SecurityConfig;
import cn.lbcmmszdntnt.security.handler.AuthFailHandler;
import cn.lbcmmszdntnt.util.thread.local.ThreadLocalMapUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-22
 * Time: 1:39
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    public void authentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        LoginUser userRecord = Optional.ofNullable(UserRecordUtil.getUserRecord(httpServletRequest))
                .orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID));
        PreAuthenticatedAuthenticationToken authenticationToken =
                new PreAuthenticatedAuthenticationToken(userRecord, null, userRecord.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        ThreadLocalMapUtil.set(SecurityConfig.USER_SECURITY_RECORD, authenticationToken);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            authentication(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            ThreadLocalMapUtil.set(AuthFailHandler.EXCEPTION_MESSAGE, e.getMessage());
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);//放行
    }
}
