package cn.lbcmmszdntnt.domain.okr.service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 17:06
 */
public interface TeamInviteService {

    String getSecret(Long teamId);

    void checkSecret(Long teamId, String secret);

}
