package cn.lbcmmszdntnt.interceptor.handler.ext.pre.authorization;

import cn.lbcmmszdntnt.common.util.convert.ObjectUtil;
import cn.lbcmmszdntnt.domain.user.enums.UserType;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
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
 * Time: 20:49
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserTypeAuthorizationPreHandler extends InterceptorHandler {

    @Override
    public Boolean condition() {
        return !InterceptorContext.isAuthorized();
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            UserType userType = InterceptorContext.getUser().getUserType();
            boolean isValid = ObjectUtil.distinctNonNullStream(InterceptorContext.getInterceptProperties().getPermit())
                    .anyMatch(type -> type.equals(userType));
            if (Boolean.TRUE.equals(isValid)) {
                InterceptorContext.setIsAuthorized(Boolean.TRUE);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
