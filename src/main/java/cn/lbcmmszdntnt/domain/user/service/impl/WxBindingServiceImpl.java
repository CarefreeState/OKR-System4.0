package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.user.service.WxBindingService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 17:24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WxBindingServiceImpl implements WxBindingService {

    private final RedisCache redisCache;

    @Override
    public String getSecret(Long userId) {
        // todo 绑定逻辑
        String randomCode = RandomUtil.randomNumbers(6);
        String redisKey = QRCodeConstants.WX_CHECK_QR_CODE_MAP + userId;
        redisCache.setObject(redisKey, randomCode,
                QRCodeConstants.WX_CHECK_QR_CODE_TTL, QRCodeConstants.WX_CHECK_QR_CODE_UNIT);
        return randomCode;
    }

    @Override
    public void checkSecret(Long userId, String secret) {
        String redisKey = QRCodeConstants.WX_CHECK_QR_CODE_MAP + userId;
        String code = redisCache.getObject(redisKey, String.class).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.WX_NOT_EXIST_RECORD));
        redisCache.deleteObject(redisKey);
        if(!secret.equals(code)) {
            // 这个随机码肯定是伪造的，因为这个请求的参数不是用户手动输入的值
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_CONSISTENT);
        }
    }
}
