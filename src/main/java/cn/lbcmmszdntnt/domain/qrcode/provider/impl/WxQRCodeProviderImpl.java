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

import static cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants.*;

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
        String scene = String.format(INVITE_CODE_SCENE_FORMAT, teamId, secret);
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
        String scene = String.format(LOGIN_CODE_SCENE_FORMAT, secret);
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
    public String getBindingQRCode(String secret) {
        WxQRCode qrCode = wxQRCodeConfig.getBinding();
        String scene = String.format(BINDING_CODE_SCENE_FORMAT, secret);
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
