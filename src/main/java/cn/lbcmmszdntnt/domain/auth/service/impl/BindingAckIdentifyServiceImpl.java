package cn.lbcmmszdntnt.domain.auth.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.auth.constants.AuthConstants;
import cn.lbcmmszdntnt.domain.auth.service.BindingAckIdentifyService;
import cn.lbcmmszdntnt.domain.auth.service.ValidateService;
import cn.lbcmmszdntnt.domain.auth.service.WxIdentifyService;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.BindingQRCodeVO;
import cn.lbcmmszdntnt.domain.qrcode.service.QRCodeService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.wxtoken.model.vo.JsCode2SessionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.lbcmmszdntnt.domain.auth.constants.AuthConstants.VALIDATE_BINDING_ACK_KEY;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 23:45
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BindingAckIdentifyServiceImpl implements BindingAckIdentifyService {

    private final RedisCache redisCache;

    private final QRCodeService qrCodeService;

    private final ValidateService validateService;

    private final WxIdentifyService wxIdentifyService;

    @Override
    public BindingQRCodeVO getBindingQRCode() {
        BindingQRCodeVO bindingQRCodeVO = qrCodeService.getBindingQRCode();
        // 设置为 "null"
        redisCache.setObject(AuthConstants.WX_BINDING_QR_CODE_MAP + bindingQRCodeVO.getSecret(), "null",
                QRCodeConstants.LOGIN_QR_CODE_TTL, QRCodeConstants.LOGIN_QR_CODE_UNIT);
        return bindingQRCodeVO;
    }

    @Override
    public void ackSecret(String secret, String code) {
        String redisKey = AuthConstants.WX_BINDING_QR_CODE_MAP + secret;
        redisCache.getObject(redisKey, String.class).ifPresentOrElse(c -> {
            if (!"null".equals(c)) {
                redisCache.setObject(redisKey, code,
                        QRCodeConstants.WX_BINDING_QR_CODE_TTL, QRCodeConstants.WX_BINDING_QR_CODE_UNIT);
            }
        }, () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_BINDING_CODE_VALID);
        });
    }

    @Override
    public JsCode2SessionVO validateSecret(String secret) {
        String redisKey = AuthConstants.LOGIN_QR_CODE_MAP + secret;
        String code = redisCache.getObject(redisKey, String.class).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID));
        validateService.validate(VALIDATE_BINDING_ACK_KEY + secret, () -> !"null".equals(code), GlobalServiceStatusCode.USER_BINDING_NOT_CHECK);
        return wxIdentifyService.validateCode(code);
    }
}
