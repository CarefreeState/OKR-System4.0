package cn.lbcmmszdntnt.domain.qrcode.provider.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.lbcmmszdntnt.common.util.media.ImageUtil;
import cn.lbcmmszdntnt.domain.media.constants.FileMediaConstants;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.qrcode.config.FontTextConfig;
import cn.lbcmmszdntnt.domain.qrcode.config.WxQRCodeConfig;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.qrcode.provider.QRCodeProvider;
import cn.lbcmmszdntnt.wxtoken.model.dto.WxQRCode;
import cn.lbcmmszdntnt.wxtoken.util.WxHttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 15:19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WxQRCodeProviderImpl implements QRCodeProvider {

    private final static Long BINDING_CODE_ACTIVE_LIMIT = QRCodeConstants.WX_CHECK_QR_CODE_UNIT.toMillis(QRCodeConstants.WX_CHECK_QR_CODE_TTL);
    private final static Long LOGIN_CODE_ACTIVE_LIMIT = QRCodeConstants.WX_LOGIN_QR_CODE_UNIT.toMillis(QRCodeConstants.WX_LOGIN_QR_CODE_TTL);

    private final static String COMMON_CODE_MESSAGE = "让目标照耀前程，用规划书写人生！";
    private final static String BINDING_CODE_MESSAGE = String.format("请在 %d %s 内前往微信扫码进行绑定！",
            QRCodeConstants.WX_CHECK_QR_CODE_TTL, QRCodeConstants.WX_CHECK_QR_CODE_UNIT);
    private final static String LOGIN_CODE_MESSAGE = String.format("请在 %d %s 内前往微信扫码进行验证！",
            QRCodeConstants.WX_LOGIN_QR_CODE_TTL, QRCodeConstants.WX_LOGIN_QR_CODE_UNIT);

    private final WxQRCodeConfig wxQRCodeConfig;

    private final FileMediaService fileMediaService;

    private final FontTextConfig fontTextConfig;

    @Override
    public <T> String getQRCode(T params, String scene, Long activeLimit, QRCodeProcessor strategy) {
        WxQRCode wxQRCode = params instanceof WxQRCode qrCode ? qrCode : BeanUtil.copyProperties(params, WxQRCode.class);
        wxQRCode.setScene(scene);
        byte[] bytes = WxHttpRequestUtil.wxQrcode(wxQRCode);
        bytes = strategy.process(bytes);
        return fileMediaService.uploadImage(QRCodeConstants.DEFAULT_ORIGINAL_NAME, bytes, activeLimit);
    }

    @Override
    public String getInviteQRCode(Long teamId, String teamName, String secret) {
        WxQRCode qrCode = wxQRCodeConfig.getInvite();
        String scene = String.format("teamId=%d&secret=%s", teamId, secret);
        return getQRCode(qrCode, scene, FileMediaConstants.DEFAULT_ACTIVE_LIMIT, bytes -> {
            return ImageUtil.signatureWrite(
                    bytes,
                    teamName,
                    fontTextConfig.getInvite(),
                    fontTextConfig.getColor()
            );
        });
    }

    @Override
    public String getCommonQRCode() {
        WxQRCode qrCode = wxQRCodeConfig.getCommon();
        String scene = "you=nice";
        return getQRCode(qrCode, scene, FileMediaConstants.DEFAULT_ACTIVE_LIMIT, bytes -> {
            return ImageUtil.signatureWrite(
                    bytes,
                    COMMON_CODE_MESSAGE,
                    fontTextConfig.getCommon(),
                    fontTextConfig.getColor()
            );
        });
    }

    @Override
    public String getLoginQRCode(String secret) {
        WxQRCode qrCode = wxQRCodeConfig.getLogin();
        String scene = String.format("secret=%s", secret);
        return getQRCode(qrCode, scene, LOGIN_CODE_ACTIVE_LIMIT, bytes -> {
            return ImageUtil.signatureWrite(
                    bytes,
                    LOGIN_CODE_MESSAGE,
                    fontTextConfig.getLogin(),
                    fontTextConfig.getColor()
            );
        });
    }

    @Override
    public String getBindingQRCode(Long userId, String secret) {
        WxQRCode qrCode = wxQRCodeConfig.getBinding();
        String scene = String.format("userId=%d&secret=%s", userId, secret);
        return getQRCode(qrCode, scene, BINDING_CODE_ACTIVE_LIMIT, bytes -> {
            return ImageUtil.signatureWrite(
                    bytes,
                    BINDING_CODE_MESSAGE,
                    fontTextConfig.getBinding(),
                    fontTextConfig.getColor()
            );
        });
    }
}
