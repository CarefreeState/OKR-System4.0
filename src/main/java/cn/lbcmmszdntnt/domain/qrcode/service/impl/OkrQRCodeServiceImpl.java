package cn.lbcmmszdntnt.domain.qrcode.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.convert.ShortCodeUtil;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.qrcode.bloomfilter.LoginSecretCodeBloomFilter;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;
import cn.lbcmmszdntnt.domain.qrcode.factory.QRCodeProviderFactory;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.LoginQRCodeVO;
import cn.lbcmmszdntnt.domain.qrcode.provider.QRCodeProvider;
import cn.lbcmmszdntnt.domain.qrcode.service.OkrQRCodeService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 21:06
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OkrQRCodeServiceImpl implements OkrQRCodeService {


    private final RedisCache redisCache;

    private final RedisLock redisLock;

    private final LoginSecretCodeBloomFilter loginSecretCodeBloomFilter;

    private final QRCodeProviderFactory qrCodeProviderFactory;

    private final FileMediaService fileMediaService;

    public String getInviteQRCode(Long teamId, String teamName, String secret, QRCodeType type) {
        String lockKey = QRCodeConstants.OKR_INVITE_QR_CODE_LOCK + teamId;
        return redisLock.tryLockGetSomething(lockKey, () -> {
            QRCodeProvider provider = qrCodeProviderFactory.getProvider(type);
            String redisKey = String.format(QRCodeConstants.TEAM_QR_CODE_MAP, type, teamId);
            return redisCache.getObject(redisKey, String.class).orElseGet(() -> {
                // 获取 QRCode
                String qrCode = provider.getInviteQRCode(teamId, teamName, secret);
                redisCache.setObject(redisKey, qrCode, QRCodeConstants.TEAM_QR_MAP_TTL, QRCodeConstants.TEAM_QR_MAP_UNIT);
                return qrCode;
            });
        }, () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }

    @Override
    public void deleteTeamNameQRCodeCache(Long teamId) {
        Arrays.stream(QRCodeType.values()).map(type -> String.format(QRCodeConstants.TEAM_QR_CODE_MAP, type.getType(), teamId))
                .forEach(redisKey -> {
                    redisCache.getObject(redisKey, String.class).ifPresent(qrCode -> {
                        redisCache.deleteObject(qrCode);
                        fileMediaService.remove(qrCode);
                    });
                });
    }

    @Override
    public String getBindingQRCode(Long userId, String secret) {
        QRCodeProvider provider = qrCodeProviderFactory.getProvider(QRCodeType.WX);
         return provider.getBindingQRCode(userId, secret);
    }

    @Override
    public LoginQRCodeVO getLoginQRCode() {
        String secret = null;
        do {
            secret = ShortCodeUtil.getShortCode(ShortCodeUtil.getSalt());
        } while (loginSecretCodeBloomFilter.contains(secret));
        loginSecretCodeBloomFilter.add(secret);
        QRCodeProvider provider = qrCodeProviderFactory.getProvider(QRCodeType.WX);
        // 设置为 -1
        redisCache.setObject(QRCodeConstants.WX_LOGIN_QR_CODE_MAP + secret, -1,
                QRCodeConstants.WX_LOGIN_QR_CODE_TTL, QRCodeConstants.WX_LOGIN_QR_CODE_UNIT);
        String qrcode = provider.getLoginQRCode(secret);
        return LoginQRCodeVO.builder()
                .path(qrcode)
                .secret(secret)
                .build();
    }

    @Override
    public String getCommonQRCode() {
        String redisKey = QRCodeConstants.WX_COMMON_QR_CODE_KEY;
        QRCodeProvider provider = qrCodeProviderFactory.getProvider(QRCodeType.WX);
        return redisLock.tryLockGetSomething(QRCodeConstants.OKR_COMMON_QR_CODE_LOCK, () ->
            redisCache.getObject(redisKey, String.class).orElseGet(() -> {
                // 获取 QRCode
                String qrcode = provider.getCommonQRCode();
                redisCache.setObject(redisKey, qrcode, QRCodeConstants.WX_COMMON_QR_CODE_TTL, QRCodeConstants.WX_COMMON_QR_CODE_UNIT);
                return qrcode;
            }), () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }

}
