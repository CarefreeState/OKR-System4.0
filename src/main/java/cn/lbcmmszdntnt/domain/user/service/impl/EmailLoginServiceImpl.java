package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.email.service.EmailService;
import cn.lbcmmszdntnt.domain.email.util.IdentifyingCodeValidator;
import cn.lbcmmszdntnt.domain.user.constants.UserPhotoConstants;
import cn.lbcmmszdntnt.domain.user.model.dto.EmailLoginDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.LoginService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    private final static String DEFAULT_NICKNAME = "邮箱用户";

    private final UserService userService;

    private final EmailService emailService;

    @Override
    public User login(LoginDTO loginDTO) {
        EmailLoginDTO emailLoginDTO = loginDTO.getEmailLoginDTO();
        if(Objects.isNull(emailLoginDTO)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String email = emailLoginDTO.getEmail();
        // 验证码验证
        emailService.checkIdentifyingCode(IdentifyingCodeValidator.EMAIL_LOGIN, email, emailLoginDTO.getCode());
        User user = new User();
        user.setEmail(email);
        // 如果用户未不存在（邮箱未注册），则注册
        userService.getUserByEmail(email)
                .ifPresentOrElse(dbUser -> {
                    user.setId(dbUser.getId());
                }, () -> {
                    user.setNickname(DEFAULT_NICKNAME);
                    user.setPhoto(UserPhotoConstants.getDefaultPhoto());
                    userService.save(user);
                    log.info("新用户注册 -> {}", user);
                });
        return user;
    }

    @Override
    public void logout(HttpServletRequest request) {
        UserRecordUtil.joinTheTokenBlacklist(request);
    }

}
