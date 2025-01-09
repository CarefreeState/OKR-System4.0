package cn.lbcmmszdntnt.domain.user.interceptor;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 15:06
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtIsInBlacklistPreHandler extends InterceptorHandler {

    @Override
    public Boolean condition() {
        return InterceptorContext.isAuthenticated();
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if(Boolean.TRUE.equals(UserRecordUtil.isInTheTokenBlacklist())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_AUTHENTICATED);
        }
    }

}
