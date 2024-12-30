package cn.lbcmmszdntnt.domain.qrcode.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.config.WebMvcConfiguration;
import cn.lbcmmszdntnt.domain.qrcode.config.properties.WxInviteQRCode;
import cn.lbcmmszdntnt.domain.qrcode.service.InviteQRCodeService;
import cn.lbcmmszdntnt.domain.qrcode.util.QRCodeUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.common.util.convert.ShortCodeUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-29
 * Time: 0:42
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxInviteQRCodeServiceImpl implements InviteQRCodeService {

    private final WxInviteQRCode wxInviteQRCode;

    @Override
    public Color getQRCodeColor() {
        return wxInviteQRCode.getQrCodeColor();
    }

    @Override
    public void checkParams(Long teamId, String secret) {
        if(Objects.isNull(teamId)) {
            throw new GlobalServiceException("团队 OKR ID 为 null", GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String sceneKey = wxInviteQRCode.getSceneKey();
        String raw = sceneKey + "=" + teamId;
        String inviteSecret = ShortCodeUtil.getShortCode(raw);
        boolean isInvited = inviteSecret.equals(secret);
        log.info("用户想要加入团队 {}, 校验：{} -> {} 与 {} 比较 -> {}", teamId, raw, inviteSecret, secret, isInvited);
        if(Boolean.FALSE.equals(isInvited)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_CANNOT_JOIN_TEAM);
        }
    }

    @Override
    public String getQRCode(Long teamId) {
        Map<String, Object> params = wxInviteQRCode.getQRCodeParams();
        StringBuilder sceneBuilder = new StringBuilder();
        // 记录一下 teamId 与 inviteSecret 关系，携带这个密钥才行
        String sceneKey = wxInviteQRCode.getSceneKey();
        String secret = wxInviteQRCode.getSecret();
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
        params.put("scene", sceneBuilder.toString());
        String json = JsonUtil.analyzeData(params);
        return MediaUtil.saveImage(QRCodeUtil.doPostGetQRCodeData(json), WebMvcConfiguration.INVITE_PATH);
    }

}
