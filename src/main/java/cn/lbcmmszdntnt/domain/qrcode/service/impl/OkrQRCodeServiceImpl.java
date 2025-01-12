package cn.lbcmmszdntnt.domain.qrcode.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.convert.ShortCodeUtil;
import cn.lbcmmszdntnt.common.util.media.ImageUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.domain.qrcode.bloomfilter.SecretCodeBloomFilter;
import cn.lbcmmszdntnt.domain.qrcode.config.QRCodeConfig;
import cn.lbcmmszdntnt.domain.qrcode.config.properties.OkrQRCode;
import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;
import cn.lbcmmszdntnt.domain.qrcode.factory.InviteQRCodeServiceFactory;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.LoginQRCodeVO;
import cn.lbcmmszdntnt.domain.qrcode.service.*;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final static String BINDING_CODE_MESSAGE = String.format("请在 %d %s 内前往微信扫码进行绑定！",
            QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);

    private final static String LOGIN_CODE_MESSAGE = String.format("请在 %d %s 内前往微信扫码进行验证！",
            QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);

    private final static String COMMON_CODE_MESSAGE = "让目标照耀前程，用规划书写人生！";

    private final OkrQRCode okrQRCode;

    private final RedisCache redisCache;

    private final RedisLock redisLock;

    private final SecretCodeBloomFilter secretCodeBloomFilter;

    private final InviteQRCodeServiceFactory inviteQRCodeServiceFactory;

    private final WxBindingQRCodeService wxBindingQRCodeService;

    private final WxLoginQRCodeService wxLoginQRCodeService;

    private final WxCommonQRCodeService wxCommonQRCodeService;

    public String getInviteQRCode(Long teamId, String teamName, QRCodeType type) {
        InviteQRCodeService inviteQRCodeService = inviteQRCodeServiceFactory.getService(type);
        String redisKey = String.format(QRCodeConfig.TEAM_QR_CODE_MAP, type, teamId);
        return redisCache.getObject(redisKey, String.class).orElseGet(() -> {
            // 获取 QRCode
            String mapPath = inviteQRCodeService.getQRCode(teamId);
            // 获取到团队名字
            String savePath = MediaUtil.getLocalFilePath(mapPath);
            ImageUtil.mergeSignatureWrite(savePath, teamName,
                    okrQRCode.getInvite(), okrQRCode.getTextColor(), inviteQRCodeService.getQRCodeColor());
            redisCache.setObject(redisKey, mapPath, QRCodeConfig.TEAM_QR_MAP_TTL, QRCodeConfig.TEAM_QR_MAP_UNIT);
            return mapPath;
        });
    }

    @Override
    public String getInviteQRCodeLock(Long teamId, String teamName, QRCodeType type) {
        String lockKey = QRCodeConfig.OKR_INVITE_QR_CODE_LOCK + teamId;
        return redisLock.tryLockGetSomething(lockKey, () -> getInviteQRCode(teamId, teamName, type), () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }

    @Override
    public void deleteTeamNameCache(Long teamId) {
        String redisKey1 = String.format(QRCodeConfig.TEAM_QR_CODE_MAP, QRCodeType.WEB.getType(), teamId);
        String redisKey2 = String.format(QRCodeConfig.TEAM_QR_CODE_MAP, QRCodeType.WX.getType(), teamId);
        redisCache.getObject(redisKey1, String.class).ifPresent(mapPath -> {
            redisCache.deleteObject(redisKey1);
            String originPath = MediaUtil.getLocalFilePath(mapPath);
            MediaUtil.deleteFile(originPath);
        });
        redisCache.getObject(redisKey2, String.class).ifPresent(mapPath -> {
            redisCache.deleteObject(redisKey2);
            String originPath = MediaUtil.getLocalFilePath(mapPath);
            MediaUtil.deleteFile(originPath);
        });
    }

    @Override
    public String getBindingQRCode(Long userId, String randomCode) {
        String redisKey = QRCodeConfig.WX_CHECK_QR_CODE_MAP + userId;
        String mapPath = wxBindingQRCodeService.getQRCode(userId, randomCode);
        redisCache.setObject(redisKey, randomCode,
                QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
        // 为图片记录缓存时间，时间一到，在服务器存储的文件应该删除掉！
        String savePath = MediaUtil.getLocalFilePath(mapPath);
        ImageUtil.mergeSignatureWrite(savePath, BINDING_CODE_MESSAGE,
                okrQRCode.getBinding(), okrQRCode.getTextColor(), wxBindingQRCodeService.getQRCodeColor());
        redisCache.setObject(QRCodeConfig.WX_CHECK_QR_CODE_CACHE + MediaUtil.getLocalFileName(mapPath), 0,
                QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
        return mapPath;
    }

    @Override
    public String getSecretCode() {
        String secret;
        do {
            secret = ShortCodeUtil.getShortCode(ShortCodeUtil.getSalt());
        } while (secretCodeBloomFilter.contains(secret));
        secretCodeBloomFilter.add(secret);
        return secret;
    }

    @Override
    public LoginQRCodeVO getLoginQRCode() {
        return getLoginQRCode(getSecretCode());
    }

    @Override
    public LoginQRCodeVO getLoginQRCode(String secret) {
        // 设置为 -1
        redisCache.setObject(QRCodeConfig.WX_LOGIN_QR_CODE_MAP + secret, -1,
                QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        // 获取一个小程序码
        String mapPath = wxLoginQRCodeService.getQRCode(secret);
        String savePath = MediaUtil.getLocalFilePath(mapPath);
        ImageUtil.mergeSignatureWrite(savePath, LOGIN_CODE_MESSAGE,
                okrQRCode.getLogin(), okrQRCode.getTextColor(), wxLoginQRCodeService.getQRCodeColor());
        redisCache.setObject(QRCodeConfig.WX_LOGIN_QR_CODE_CACHE + MediaUtil.getLocalFileName(mapPath), 0,
                QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
        return LoginQRCodeVO.builder()
                .path(mapPath)
                .secret(secret)
                .build();
    }

    @Override
    public String getCommonQRCode() {
        String redisKey = QRCodeConfig.WX_COMMON_QR_CODE_KEY;
        return redisLock.tryLockGetSomething(QRCodeConfig.OKR_COMMON_QR_CODE_LOCK, () ->
            redisCache.getObject(redisKey, String.class).orElseGet(() -> {
                // 获取 QRCode
                String mapPath = wxCommonQRCodeService.getQRCode();
                String savePath = MediaUtil.getLocalFilePath(mapPath);
                ImageUtil.mergeSignatureWrite(savePath, COMMON_CODE_MESSAGE,
                        okrQRCode.getCommon(), okrQRCode.getTextColor(), wxCommonQRCodeService.getQRCodeColor());
                redisCache.setObject(redisKey, mapPath, QRCodeConfig.WX_COMMON_QR_CODE_TTL, QRCodeConfig.WX_COMMON_QR_CODE_UNIT);
                return mapPath;
            }), () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }

}
