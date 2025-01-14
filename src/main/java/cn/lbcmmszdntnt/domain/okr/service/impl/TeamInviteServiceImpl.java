package cn.lbcmmszdntnt.domain.okr.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.convert.ShortCodeUtil;
import cn.lbcmmszdntnt.domain.okr.service.TeamInviteService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
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
public class TeamInviteServiceImpl implements TeamInviteService {

    @Override
    public String getSecret(Long teamId) {
        return ShortCodeUtil.getShortCode(String.format("teamId=%d", teamId));
    }

    @Override
    public void checkSecret(Long teamId, String secret) {
        boolean isInvited = getSecret(teamId).equals(secret);
        log.info("用户想要加入团队 {}, 校验：{}, {}", teamId, secret, isInvited);
        if(Boolean.FALSE.equals(isInvited)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_CANNOT_JOIN_TEAM);
        }
    }
}
