package cn.lbcmmszdntnt.domain.qrcode.provider.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.lbcmmszdntnt.common.util.media.ImageUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.common.util.web.HttpRequestUtil;
import cn.lbcmmszdntnt.domain.media.constants.FileMediaConstants;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.qrcode.config.FontTextConfig;
import cn.lbcmmszdntnt.domain.qrcode.config.WebQRCodeConfig;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.qrcode.model.dto.WebQRCode;
import cn.lbcmmszdntnt.domain.qrcode.provider.QRCodeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        String url = HttpRequestUtil.buildUrl(webQRCode.getPage(), Map.of("scene", List.of(scene)));
        Integer width = webQRCode.getWidth();
        log.info("生成二维码 -> {}  {}  {} ", url, width, width);
        byte[] codeBytes = MediaUtil.getUrlQRCodeBytes(url, width, width);
        codeBytes = strategy.process(codeBytes);
        return fileMediaService.uploadImage(QRCodeConstants.DEFAULT_ORIGINAL_NAME, codeBytes, activeLimit);
    }

    @Override
    public String getInviteQRCode(Long teamId, String teamName, String secret) {
        WebQRCode qrCode = webQRCodeConfig.getInvite();
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
        return null;
    }

    @Override
    public String getLoginQRCode(String secret) {
        return null;
    }

    @Override
    public String getBindingQRCode(Long userId, String secret) {
        return null;
    }
}
