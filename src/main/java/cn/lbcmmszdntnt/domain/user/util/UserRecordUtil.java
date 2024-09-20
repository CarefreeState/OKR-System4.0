package cn.lbcmmszdntnt.domain.user.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.aop.config.PreInterceptConfig;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.factory.UserRecordServiceFactory;
import cn.lbcmmszdntnt.domain.user.model.dto.detail.LoginUser;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.service.UserRecordService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.security.config.SecurityConfig;
import cn.lbcmmszdntnt.util.thread.local.ThreadLocalMapUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;
import java.util.Optional;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 19:58
 */
@Component
@Slf4j
public class UserRecordUtil {

    private final static UserRecordServiceFactory USER_RECORD_SERVICE_FACTORY = SpringUtil.getBean(UserRecordServiceFactory.class);

    public static UserRecordService selectService(HttpServletRequest request) {
        String type = request.getHeader(PreInterceptConfig.HEADER);
        if(!StringUtils.hasText(type)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID);
        }
        return USER_RECORD_SERVICE_FACTORY.getService(type);
    }

    public static PreAuthenticatedAuthenticationToken getAuthenticationToken() {
        return ThreadLocalMapUtil.get(SecurityConfig.USER_SECURITY_RECORD, PreAuthenticatedAuthenticationToken.class);
    }

    public static PreAuthenticatedAuthenticationToken getAuthenticationToken(HttpServletRequest request) {
        // 若存在记录了就用之前的，避免重复进行业务
        return Optional.ofNullable(getAuthenticationToken()).orElseGet(() -> {
            LoginUser userRecord = selectService(request).getRecord(request).orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID));
            PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(userRecord, null, userRecord.getAuthorities());
            ThreadLocalMapUtil.set(SecurityConfig.USER_SECURITY_RECORD, authenticationToken);
            return authenticationToken;
        });
    }

    public static User getUserRecord() {
        LoginUser loginUser = (LoginUser) getAuthenticationToken().getPrincipal();
        return loginUser.getUser();
    }

    public static void deleteUserRecord() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            HttpServletRequest request = attributes.getRequest();
            selectService(request).deleteRecord(request);
        }
    }
}
