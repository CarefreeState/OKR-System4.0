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

    public static LoginUser getUserRecord(HttpServletRequest request) {
        return selectService(request).getRecord(request).orElse(null);
    }

    public static User getUserRecord() {
        LoginUser loginUser = (LoginUser) ThreadLocalMapUtil.get(SecurityConfig.USER_SECURITY_RECORD,
                PreAuthenticatedAuthenticationToken.class).getPrincipal();
        return loginUser.getUser();
    }

    public static void deleteUserRecord() {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (Objects.nonNull(attributes)) {
//            HttpServletRequest request = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes().getRequest();
//            selectService(request).deleteRecord(request);
//        }
        HttpServletRequest request = ThreadLocalMapUtil.get(SecurityConfig.HTTP_SERVLET_REQUEST, HttpServletRequest.class);
        selectService(request).deleteRecord(request);
    }
}
