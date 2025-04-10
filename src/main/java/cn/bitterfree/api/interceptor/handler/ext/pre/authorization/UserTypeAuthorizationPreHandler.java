package cn.bitterfree.api.interceptor.handler.ext.pre.authorization;

import cn.bitterfree.api.common.util.convert.ObjectUtil;
import cn.bitterfree.api.domain.user.enums.UserType;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.interceptor.context.InterceptorContext;
import cn.bitterfree.api.interceptor.handler.InterceptorHandler;
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
    public Boolean condition(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return !InterceptorContext.isAuthorized();
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            User user = InterceptorContext.getUser();
            UserType userType = user.getUserType();
            boolean isValid = ObjectUtil.distinctNonNullStream(InterceptorContext.getInterceptProperties().getPermit())
                    .anyMatch(type -> type.equals(userType));
            log.info("对当前用户授权 {}", user);
            if (Boolean.TRUE.equals(isValid)) {
                log.info("当前用户授权成功");
                InterceptorContext.setIsAuthorized(Boolean.TRUE);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
