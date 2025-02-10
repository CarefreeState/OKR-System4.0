package cn.bitterfree.domain.auth.service.impl;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.domain.auth.constants.AuthConstants;
import cn.bitterfree.domain.auth.generator.LoginShortCodeGenerator;
import cn.bitterfree.domain.auth.service.LoginAckIdentifyService;
import cn.bitterfree.domain.auth.service.ValidateService;
import cn.bitterfree.domain.qrcode.constants.QRCodeConstants;
import cn.bitterfree.domain.qrcode.enums.QRCodeType;
import cn.bitterfree.domain.qrcode.model.vo.LoginQRCodeVO;
import cn.bitterfree.domain.qrcode.service.QRCodeService;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.service.UserService;
import cn.bitterfree.redis.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.bitterfree.domain.auth.constants.AuthConstants.VALIDATE_LOGIN_ACK_KEY;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 17:04
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LoginAckIdentifyServiceImpl implements LoginAckIdentifyService {

    private final QRCodeService qrCodeService;

    private final UserService userService;

    private final ValidateService validateService;

    private final RedisCache redisCache;

    private final LoginShortCodeGenerator loginShortCodeGenerator;

    @Override
    public String getSecret() {
        return loginShortCodeGenerator.generate();
    }

    @Override
    public LoginQRCodeVO getLoginQRCode(String secret, QRCodeType codeType) {
        String path = qrCodeService.getLoginQRCode(secret, codeType);
        return LoginQRCodeVO.builder()
                .path(path)
                .secret(secret)
                .build();
    }

    @Override
    public void ackSecret(String secret, Long userId) {
        String redisKey = AuthConstants.LOGIN_QR_CODE_MAP + secret;
        redisCache.getObject(redisKey, Long.class).ifPresentOrElse(uid -> {
            if (uid.compareTo(0L) <= 0) {
                redisCache.setObject(redisKey, userId,
                        QRCodeConstants.LOGIN_QR_CODE_TTL, QRCodeConstants.LOGIN_QR_CODE_UNIT);
            }
        }, () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID);
        });
    }

    @Override
    public User validateSecret(String secret) {
        String redisKey = AuthConstants.LOGIN_QR_CODE_MAP + secret;
        Long userId = redisCache.getObject(redisKey, Long.class).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID));
        validateService.validate(VALIDATE_LOGIN_ACK_KEY + secret, () -> userId.compareTo(0L) > 0, GlobalServiceStatusCode.USER_LOGIN_NOT_CHECK);
        return userService.getUserById(userId)
                .orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID));
    }
}
