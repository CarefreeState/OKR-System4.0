package cn.lbcmmszdntnt.domain.teaminvite.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.auth.service.ValidateService;
import cn.lbcmmszdntnt.domain.okr.util.TeamOkrUtil;
import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;
import cn.lbcmmszdntnt.domain.qrcode.service.QRCodeService;
import cn.lbcmmszdntnt.domain.teaminvite.constants.TeamInviteConstants;
import cn.lbcmmszdntnt.domain.teaminvite.service.TeamInviteIdentifyService;
import cn.lbcmmszdntnt.shortcode.NormalShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 17:07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamInviteIdentifyServiceImpl implements TeamInviteIdentifyService {

    private final NormalShortCodeGenerator normalShortCodeGenerator;

    private final QRCodeService qrCodeService;

    private final ValidateService validateService;

    private String convert(Long teamId) {
        return normalShortCodeGenerator.convert(String.format(TeamInviteConstants.INVITE_SECRET_ORIGINAL_FORMAT, teamId));
    }

    @Override
    public String getInviteQRCode(Long teamId, QRCodeType qrCodeType) {
        return qrCodeService.getInviteQRCode(
                teamId,
                TeamOkrUtil.getTeamName(teamId),
                convert(teamId),
                qrCodeType);
    }

    @Override
    public void validateSecret(Long userId, Long teamId, String secret) {
        validateService.validate(String.format(TeamInviteConstants.VALIDATE_TEAM_INVITE_KEY, teamId, userId), () -> {
            boolean isInvited = convert(teamId).equals(secret);
            log.info("用户想要加入团队 {}, 校验：{}, {}", teamId, secret, isInvited);
            return isInvited;
        }, GlobalServiceStatusCode.USER_CANNOT_JOIN_TEAM);
    }
}
