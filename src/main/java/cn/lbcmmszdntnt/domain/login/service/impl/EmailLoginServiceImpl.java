package cn.lbcmmszdntnt.domain.login.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.auth.enums.EmailIdentifyType;
import cn.lbcmmszdntnt.domain.auth.service.EmailIdentifyService;
import cn.lbcmmszdntnt.domain.login.model.dto.EmailLoginDTO;
import cn.lbcmmszdntnt.domain.login.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.login.service.LoginService;
import cn.lbcmmszdntnt.domain.user.constants.UserConstants;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserPhotoService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 13:18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailLoginServiceImpl implements LoginService {

    private final RedisLock redisLock;

    private final UserService userService;

    private final UserPhotoService userPhotoService;

    private final EmailIdentifyService emailIdentifyService;

    @Override
    @Transactional
    public User login(LoginDTO loginDTO) {
        EmailLoginDTO emailLoginDTO = Optional.ofNullable(loginDTO.getEmailLoginDTO()).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.PARAM_IS_BLANK));
        String email = emailLoginDTO.getEmail();
        // 验证码验证
        emailIdentifyService.validateEmailCode(EmailIdentifyType.LOGIN, email, emailLoginDTO.getCode());
        // 如果用户不存在（邮箱用户未注册/绑定），则注册
        return redisLock.tryLockGetSomething(UserConstants.EXISTS_USER_EMAIL_LOCK + email, () -> {
            return userService.getUserByEmail(email).orElseGet(() -> {
                User user = new User();
                user.setEmail(email);
                user.setUsername(email);
                user.setNickname(UserConstants.DEFAULT_EMAIL_USER_NICKNAME);
                user.setPhoto(userPhotoService.getAnyOnePhoto());
                user.setUserType(UserConstants.DEFAULT_USER_TYPE);
                return userService.registerUser(user);
            });
        }, () -> null);
    }

}
