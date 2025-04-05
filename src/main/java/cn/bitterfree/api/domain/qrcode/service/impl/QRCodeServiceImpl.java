package cn.bitterfree.api.domain.qrcode.service.impl;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.media.service.FileMediaService;
import cn.bitterfree.api.domain.qrcode.constants.QRCodeConstants;
import cn.bitterfree.api.domain.qrcode.enums.QRCodeType;
import cn.bitterfree.api.domain.qrcode.factory.QRCodeProviderFactory;
import cn.bitterfree.api.domain.qrcode.provider.QRCodeProvider;
import cn.bitterfree.api.domain.qrcode.service.QRCodeService;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.redis.lock.RedisLock;
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
public class QRCodeServiceImpl implements QRCodeService {

    private final RedisCache redisCache;

    private final RedisLock redisLock;

    private final QRCodeProviderFactory qrCodeProviderFactory;

    private final FileMediaService fileMediaService;

    public String getInviteQRCode(Long teamId, String teamName, String secret, QRCodeType type) {
        String lockKey = QRCodeConstants.OKR_INVITE_QR_CODE_LOCK + teamId;
        return redisLock.tryLockGetSomething(lockKey, () -> {
            QRCodeProvider provider = qrCodeProviderFactory.getProvider(type);
            String redisKey = String.format(QRCodeConstants.TEAM_INVITE_QR_CODE_MAP, type, teamId);
            return redisCache.getObject(redisKey, String.class).orElseGet(() -> {
                // 获取 QRCode
                String qrCode = provider.getInviteQRCode(teamId, teamName, secret);
                redisCache.setObject(redisKey, qrCode, QRCodeConstants.TEAM_INVITE_QR_MAP_TTL, QRCodeConstants.TEAM_INVITE_QR_MAP_UNIT);
                return qrCode;
            });
        }, () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }

    @Override
    public void deleteTeamNameQRCodeCache(Long teamId) {
        Arrays.stream(QRCodeType.values()).map(type -> String.format(QRCodeConstants.TEAM_INVITE_QR_CODE_MAP, type, teamId))
                .forEach(redisKey -> {
                    redisCache.getObject(redisKey, String.class).ifPresent(fileMediaService::remove);
                    redisCache.deleteObject(redisKey);
                });
    }

    @Override
    public String getBindingQRCode(String secret) {
        QRCodeProvider provider = qrCodeProviderFactory.getProvider(QRCodeType.WX);
        return provider.getBindingQRCode(secret);
    }

    @Override
    public String getLoginQRCode(String secret, QRCodeType type) {
        QRCodeProvider provider = qrCodeProviderFactory.getProvider(type);
        return provider.getLoginQRCode(secret);
    }

    @Override
    public String getCommonQRCode() {
        String redisKey = QRCodeConstants.WX_COMMON_QR_CODE_KEY;
        QRCodeProvider provider = qrCodeProviderFactory.getProvider(QRCodeType.WX);
        return redisLock.tryLockGetSomething(QRCodeConstants.OKR_COMMON_QR_CODE_LOCK, () ->
            redisCache.getObject(redisKey, String.class).orElseGet(() -> {
                // 获取 QRCode
                String qrcode = provider.getCommonQRCode();
                redisCache.setObject(redisKey, qrcode, QRCodeConstants.COMMON_QR_CODE_TTL, QRCodeConstants.COMMON_QR_CODE_UNIT);
                return qrcode;
            }), () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }

}
