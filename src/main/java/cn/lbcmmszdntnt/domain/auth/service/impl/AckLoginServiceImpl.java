package cn.lbcmmszdntnt.domain.auth.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.auth.model.dto.AckLoginDTO;
import cn.lbcmmszdntnt.domain.auth.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.auth.service.LoginService;
import cn.lbcmmszdntnt.domain.qrcode.config.QRCodeConfig;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 17:39
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AckLoginServiceImpl implements LoginService {

    private final RedisCache redisCache;

    private final UserService userService;

    @Override
    public User login(LoginDTO loginDTO) {
        AckLoginDTO ackLoginDTO = loginDTO.getAckLoginDTO();
        if(Objects.isNull(ackLoginDTO)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String redisKey = QRCodeConfig.WX_LOGIN_QR_CODE_MAP + ackLoginDTO.getSecret();
        return redisCache.getObject(redisKey, Long.class).map(uid -> {
            if(uid.compareTo(0L) <= 0) {
                throw new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_NOT_CHECK);
            }
            // 删除缓存
            redisCache.deleteObject(redisKey);
            return userService.getUserById(uid).orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.USER_ACCOUNT_NOT_EXIST));
        }).orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID));
    }
}
