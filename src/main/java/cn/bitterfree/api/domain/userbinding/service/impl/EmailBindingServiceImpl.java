package cn.bitterfree.api.domain.userbinding.service.impl;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.auth.enums.EmailIdentifyType;
import cn.bitterfree.api.domain.auth.service.EmailIdentifyService;
import cn.bitterfree.api.domain.user.constants.UserConstants;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.domain.user.service.UserService;
import cn.bitterfree.api.domain.userbinding.handler.chain.UserMergeHandlerChain;
import cn.bitterfree.api.domain.userbinding.model.dto.BindingDTO;
import cn.bitterfree.api.domain.userbinding.model.dto.EmailBindingDTO;
import cn.bitterfree.api.domain.userbinding.service.BindingService;
import cn.bitterfree.api.redis.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final UserMergeHandlerChain userMergeHandlerChain;

    @Override
    @Transactional
    public void binding(User user, BindingDTO bindingDTO) {
        EmailBindingDTO emailBindingDTO = Optional.ofNullable(bindingDTO.getEmailBindingDTO()).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.PARAM_IS_BLANK));
        // 判断当前用户是否绑定了邮箱（如果支持重新绑定，一定要注意用户名是否需要进一步的调整）
        if(StringUtils.hasText(user.getEmail())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_BOUND_EMAIL);
        }
        // 验证码验证
        Long userId = user.getId();
        String email = emailBindingDTO.getEmail();
        emailIdentifyService.validateEmailCode(EmailIdentifyType.BINDING, email, emailBindingDTO.getCode());
        // 若没人使用这个邮箱就绑定
        redisLock.tryLockDoSomething(UserConstants.EXISTS_USER_EMAIL_LOCK + email, () -> {
            userService.getUserByEmail(email).ifPresentOrElse(emailUser -> {
                if(!emailUser.getUserType().equals(user.getUserType())) {
                    throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_PERMISSION);
                }
                // merge
                Long emailUserId = emailUser.getId();
                if(userId.equals(emailUserId)) {
                    throw new GlobalServiceException(GlobalServiceStatusCode.EMAIL_USER_BE_BOUND);
                }
                userMergeHandlerChain.handle(userId, emailUserId);
                log.info("用户 {} 成功合并邮箱账号 {}", userId, email);
            }, () -> {
                // 绑定
                log.info("用户 {} 成功绑定邮箱 {}", userId, email);
                userService.lambdaUpdate().eq(User::getId, userId).set(User::getEmail, email).update();
            });
        }, () -> {});

    }
}
