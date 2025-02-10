package cn.bitterfree.domain.userbinding.service.impl;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.domain.auth.enums.EmailIdentifyType;
import cn.bitterfree.domain.auth.service.EmailIdentifyService;
import cn.bitterfree.domain.user.constants.UserConstants;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.service.UserService;
import cn.bitterfree.domain.userbinding.model.dto.BindingDTO;
import cn.bitterfree.domain.userbinding.model.dto.EmailBindingDTO;
import cn.bitterfree.domain.userbinding.service.BindingService;
import cn.bitterfree.redis.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 22:03
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailBindingServiceImpl implements BindingService {

    private final RedisLock redisLock;

    private final UserService userService;

    private final EmailIdentifyService emailIdentifyService;

    @Override
    public void binding(User user, BindingDTO bindingDTO) {
        EmailBindingDTO emailBindingDTO = Optional.ofNullable(bindingDTO.getEmailBindingDTO()).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.PARAM_IS_BLANK));
        // 判断当前用户是否绑定了邮箱
        if(StringUtils.hasText(user.getEmail())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_BOUND_EMAIL);
        }
        // 验证码验证
        String email = emailBindingDTO.getEmail();
        emailIdentifyService.validateEmailCode(EmailIdentifyType.LOGIN, email, emailBindingDTO.getCode());
        // 若没人使用这个邮箱就绑定
        redisLock.tryLockDoSomething(UserConstants.EXISTS_USER_EMAIL_LOCK + email, () -> {
            userService.getUserByEmail(email).ifPresentOrElse(emailUser -> {
                throw new GlobalServiceException(GlobalServiceStatusCode.EMAIL_USER_BE_BOUND);
            }, () -> {
                Long userId = user.getId();
                // 绑定
                log.info("用户 {} 成功绑定邮箱 {}", userId, email);
                userService.lambdaUpdate().eq(User::getId, userId).set(User::getEmail, email).update();
            });
        }, () -> {});

    }
}
