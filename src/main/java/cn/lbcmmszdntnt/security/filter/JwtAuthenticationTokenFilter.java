package cn.lbcmmszdntnt.security.filter;

import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.security.config.SecurityConfig;
import cn.lbcmmszdntnt.util.thread.local.ThreadLocalMapUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            SecurityContextHolder.getContext().setAuthentication(UserRecordUtil.getAuthenticationToken(httpServletRequest));
        } catch (Exception e) {
            ThreadLocalMapUtil.append(SecurityConfig.EXCEPTION_MESSAGE, e.getMessage());
        }
        //放行（放行两次会导致，另一次是另一个线程，并且请求参数之类的全空，也不会再走 SpringSecurity 的逻辑，doFilter 内部几乎干完了我们的 SpringMVC 的请求，再调用一次没意义）
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
