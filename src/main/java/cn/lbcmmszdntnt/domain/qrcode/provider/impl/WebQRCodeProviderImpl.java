package cn.lbcmmszdntnt.domain.qrcode.provider.impl;

import cn.lbcmmszdntnt.common.util.media.ImageUtil;
import cn.lbcmmszdntnt.domain.media.constants.FileMediaConstants;
import cn.lbcmmszdntnt.domain.qrcode.config.FontTextConfig;
import cn.lbcmmszdntnt.domain.qrcode.config.WebQRCodeConfig;
import cn.lbcmmszdntnt.domain.qrcode.model.dto.WebQRCode;
import cn.lbcmmszdntnt.domain.qrcode.provider.QRCodeProvider;
import cn.lbcmmszdntnt.domain.qrcode.service.impl.WebQRCodeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final WebQRCodeServiceImpl qrCodeService;

    private final FontTextConfig fontTextConfig;

    @Override
    public String getInviteQRCode(Long teamId, String teamName, String secret) {
        WebQRCode qrCode = webQRCodeConfig.getInvite();
        String scene = String.format("teamId=%d&secret=%s", teamId, secret);
        return qrCodeService.getQRCode(qrCode, scene, FileMediaConstants.DEFAULT_ACTIVE_LIMIT, bytes -> {
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
