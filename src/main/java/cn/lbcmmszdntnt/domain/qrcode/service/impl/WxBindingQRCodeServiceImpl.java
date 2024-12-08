package cn.lbcmmszdntnt.domain.qrcode.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.config.WebMvcConfiguration;
import cn.lbcmmszdntnt.domain.qrcode.config.QRCodeConfig;
import cn.lbcmmszdntnt.domain.qrcode.config.properties.WxBindingQRCode;
import cn.lbcmmszdntnt.domain.qrcode.service.WxBindingQRCodeService;
import cn.lbcmmszdntnt.domain.qrcode.util.QRCodeUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import cn.lbcmmszdntnt.util.media.MediaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 19:36
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WxBindingQRCodeServiceImpl implements WxBindingQRCodeService {

    private final WxBindingQRCode wxBindingQRCode;

    private final RedisCache redisCache;

    @Override
    public Color getQRCodeColor() {
        return wxBindingQRCode.getQrCodeColor();
    }

    @Override
    public String getQRCode(Long userId, String randomCode) {
        Map<String, Object> params = wxBindingQRCode.getQRCodeParams();
        String userKey = wxBindingQRCode.getUserKey();
        String secret = wxBindingQRCode.getSecret();
        String scene = String.format("%s=%d&%s=%s", userKey, userId, secret, randomCode);
        params.put("scene", scene);
        String json = JsonUtil.analyzeData(params);
        return MediaUtil.saveImage(QRCodeUtil.doPostGetQRCodeData(json), WebMvcConfiguration.BINDING_PATH);
    }

    @Override
    public void checkParams(Long userId, String randomCode) {
        String redisKey = QRCodeConfig.WX_CHECK_QR_CODE_MAP + userId;
        String code = (String) redisCache.getObject(redisKey, String.class).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.WX_NOT_EXIST_RECORD));
        redisCache.deleteObject(redisKey);
        if(!randomCode.equals(code)) {
            // 这个随机码肯定是伪造的，因为这个请求的参数不是用户手动输入的值
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_CONSISTENT);
        }
    }

}
