package cn.lbcmmszdntnt.domain.auth.service.impl;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.auth.constants.AuthConstants;
import cn.lbcmmszdntnt.domain.auth.service.PasswordIdentifyService;
import cn.lbcmmszdntnt.domain.auth.service.ValidateService;
import cn.lbcmmszdntnt.domain.auth.util.PasswordMd5WithSaltUtil;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-13
 * Time: 0:29
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordIdentifyServiceImpl implements PasswordIdentifyService {

    private final ValidateService validateService;

    private final UserService userService;

    @Override
    public String passwordEncrypt(String password) {
        return PasswordMd5WithSaltUtil.encrypt(password);
    }

    @Override
    public User validatePassword(String username, String password) {
        User user = userService.checkAndGetUserByUsername(username);
        validateService.validate(AuthConstants.VALIDATE_PASSWORD_KEY + username, () -> {
            String dbPassword = user.getPassword();
            return PasswordMd5WithSaltUtil.confirm(password, dbPassword);
        }, GlobalServiceStatusCode.USER_USERNAME_PASSWORD_ERROR);
        return user;
    }
}
