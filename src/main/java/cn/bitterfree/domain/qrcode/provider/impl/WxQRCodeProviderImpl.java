package cn.bitterfree.domain.qrcode.provider.impl;

import cn.bitterfree.common.util.media.ImageUtil;
import cn.bitterfree.domain.media.service.FileMediaService;
import cn.bitterfree.domain.qrcode.config.FontTextConfig;
import cn.bitterfree.domain.qrcode.config.WxQRCodeConfig;
import cn.bitterfree.domain.qrcode.constants.QRCodeConstants;
import cn.bitterfree.domain.qrcode.provider.QRCodeProvider;
import cn.bitterfree.wxtoken.model.dto.WxQRCode;
import cn.bitterfree.wxtoken.util.WxHttpRequestUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.bitterfree.domain.qrcode.constants.QRCodeConstants.*;

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
        return getQRCode(qrCode, scene, INVITE_CODE_ACTIVE_LIMIT, bytes -> {
            return ImageUtil.signatureWrite(
                    bytes,
                    teamName,
                    fontTextConfig.getInvite(),
                    fontTextConfig.getColor(),
                    qrCode.getLineColor().color()
            );
        });
    }

    @Override
    public String getCommonQRCode() {
        WxQRCode qrCode = wxQRCodeConfig.getCommon();
        String scene = "you=nice";
        return getQRCode(qrCode, scene, QRCodeConstants.COMMON_CODE_ACTIVE_LIMIT, bytes -> {
            return ImageUtil.signatureWrite(
                    bytes,
                    COMMON_CODE_MESSAGE,
                    fontTextConfig.getCommon(),
                    fontTextConfig.getColor(),
                    qrCode.getLineColor().color()
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
                    fontTextConfig.getColor(),
                    qrCode.getLineColor().color()
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
                    fontTextConfig.getColor(),
                    qrCode.getLineColor().color()
            );
        });
    }
}
