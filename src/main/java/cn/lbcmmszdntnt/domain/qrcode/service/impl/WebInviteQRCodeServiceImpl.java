package cn.lbcmmszdntnt.domain.qrcode.service.impl;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.config.WebMvcConfiguration;
import cn.lbcmmszdntnt.domain.qrcode.config.properties.WebInviteQRCode;
import cn.lbcmmszdntnt.domain.qrcode.service.InviteQRCodeService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.util.convert.ShortCodeUtil;
import cn.lbcmmszdntnt.util.media.MediaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-22
 * Time: 18:36
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebInviteQRCodeServiceImpl implements InviteQRCodeService {

    private final WebInviteQRCode webInviteQRCode;

    @Override
    public Color getQRCodeColor() {
        return this.webInviteQRCode.getQrCodeColor();
    }

    @Override
    public String getQRCode(Long teamId) {
        StringBuilder sceneBuilder = new StringBuilder();
        // 记录一下 teamId 与 inviteSecret 关系，携带这个密钥才行
        String sceneKey = webInviteQRCode.getSceneKey();
        String secret = webInviteQRCode.getSecret();
        Integer width = webInviteQRCode.getWidth();
        String page = webInviteQRCode.getPage();
        sceneBuilder
                .append(sceneKey)
                .append("=")
                .append(teamId);
        // 短码虽然无法保证绝对的唯一，但是 teamId 能确定短码即可
        String inviteSecret = ShortCodeUtil.getShortCode(sceneBuilder.toString());
        sceneBuilder
                .append("&")
                .append(secret)
                .append("=")
                .append(inviteSecret);
        String url = page + "?" + sceneBuilder;
        log.info("生成二维码 -> {}  {}  {} ", url, width, width);
        byte[] codeBytes = MediaUtil.getCustomColorQRCodeByteArray(url, width, width);
        return MediaUtil.saveImage(codeBytes, WebMvcConfiguration.INVITE_PATH);
    }

    @Override
    public void checkParams(Long teamId, String secret) {
        if(Objects.isNull(teamId)) {
            throw new GlobalServiceException("团队 OKR ID 为 null", GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String raw = webInviteQRCode.getSceneKey() + "=" + teamId;
        String inviteSecret = ShortCodeUtil.getShortCode(raw);
        boolean isInvited = inviteSecret.equals(secret);
        log.info("用户想要加入团队 {}, 校验：{} -> {} 与 {} 比较 -> {}", teamId, raw, inviteSecret, secret, isInvited);
        if(Boolean.FALSE.equals(isInvited)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_CANNOT_JOIN_TEAM);
        }
    }

}
