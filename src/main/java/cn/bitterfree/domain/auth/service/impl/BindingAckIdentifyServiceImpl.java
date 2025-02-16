package cn.bitterfree.domain.auth.service.impl;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.domain.auth.constants.AuthConstants;
import cn.bitterfree.domain.auth.generator.BindingShortCodeGenerator;
import cn.bitterfree.domain.auth.service.BindingAckIdentifyService;
import cn.bitterfree.domain.auth.service.ValidateService;
import cn.bitterfree.domain.auth.service.WxIdentifyService;
import cn.bitterfree.domain.qrcode.constants.QRCodeConstants;
import cn.bitterfree.domain.qrcode.model.vo.BindingQRCodeVO;
import cn.bitterfree.domain.qrcode.service.QRCodeService;
import cn.bitterfree.redis.cache.RedisCache;
import cn.bitterfree.wxtoken.model.vo.JsCode2SessionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.bitterfree.domain.auth.constants.AuthConstants.VALIDATE_BINDING_ACK_KEY;

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

    private final BindingShortCodeGenerator bindingShortCodeGenerator;

    @Override
    public String getSecret() {
        return bindingShortCodeGenerator.generate();
    }

    @Override
    public BindingQRCodeVO getBindingQRCode(String secret) {
        String path = qrCodeService.getBindingQRCode(secret);
        return BindingQRCodeVO.builder()
                .path(path)
                .secret(secret)
                .build();
    }

    @Override
    public void ackSecret(String secret, String code) {
        String redisKey = AuthConstants.WX_BINDING_QR_CODE_MAP + secret;
        redisCache.getObject(redisKey, String.class).ifPresentOrElse(c -> {
            if ("null".equals(c)) {
                redisCache.setObject(redisKey, code,
                        QRCodeConstants.WX_BINDING_QR_CODE_TTL, QRCodeConstants.WX_BINDING_QR_CODE_UNIT);
            } else {
                throw new GlobalServiceException(GlobalServiceStatusCode.USER_BINDING_CHECKED);
            }
        }, () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_BINDING_CODE_VALID);
        });
    }

    @Override
    public JsCode2SessionVO validateSecret(String secret) {
        String redisKey = AuthConstants.WX_BINDING_QR_CODE_MAP + secret;
        String code = redisCache.getObject(redisKey, String.class).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.USER_LOGIN_CODE_VALID));
        validateService.validate(VALIDATE_BINDING_ACK_KEY + secret, () -> !"null".equals(code), GlobalServiceStatusCode.USER_BINDING_NOT_CHECK);
        JsCode2SessionVO jsCode2SessionVO = wxIdentifyService.validateCode(code);
        redisCache.deleteObject(redisKey);
        return jsCode2SessionVO;
    }
}
