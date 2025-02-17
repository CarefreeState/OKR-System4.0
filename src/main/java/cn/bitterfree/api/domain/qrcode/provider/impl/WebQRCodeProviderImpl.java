package cn.bitterfree.api.domain.qrcode.provider.impl;

import cn.bitterfree.api.common.util.media.ImageUtil;
import cn.bitterfree.api.common.util.web.HttpRequestUtil;
import cn.bitterfree.api.domain.media.service.FileMediaService;
import cn.bitterfree.api.domain.qrcode.config.FontTextConfig;
import cn.bitterfree.api.domain.qrcode.config.WebQRCodeConfig;
import cn.bitterfree.api.domain.qrcode.constants.QRCodeConstants;
import cn.bitterfree.api.domain.qrcode.model.dto.WebQRCode;
import cn.bitterfree.api.domain.qrcode.provider.QRCodeProvider;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static cn.bitterfree.api.domain.qrcode.constants.QRCodeConstants.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 15:18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebQRCodeProviderImpl implements QRCodeProvider {

    private final WebQRCodeConfig webQRCodeConfig;

    private final FileMediaService fileMediaService;

    private final FontTextConfig fontTextConfig;

    @Override
    public <T> String getQRCode(T params, String scene, Long activeLimit, QRCodeProcessor strategy) {
        WebQRCode webQRCode = params instanceof WebQRCode qrCode ? qrCode : BeanUtil.copyProperties(params, WebQRCode.class);
        String url = HttpRequestUtil.buildUrl(webQRCode.getPage(), Map.of(WEB_QR_CODE_SCENE_KEY, List.of(scene)));
        Integer width = webQRCode.getWidth();
        log.info("生成二维码 -> {}  {}  {} ", url, width, width);
        byte[] codeBytes = ImageUtil.getUrlQRCodeBytes(url, width, width, webQRCode.getLineColor().color());
        codeBytes = strategy.process(codeBytes);
        return fileMediaService.uploadImage(QRCodeConstants.DEFAULT_ORIGINAL_NAME, codeBytes, activeLimit);
    }

    @Override
    public String getInviteQRCode(Long teamId, String teamName, String secret) {
        WebQRCode qrCode = webQRCodeConfig.getInvite();
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
        return null;
    }

    @Override
    public String getLoginQRCode(String secret) {
        WebQRCode qrCode = webQRCodeConfig.getLogin();
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
        return null;
    }
}
