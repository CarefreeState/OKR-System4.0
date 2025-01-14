package cn.lbcmmszdntnt.domain.user.service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 17:20
 */
public interface WxBindingService {

    String getSecret(Long userId);

    void checkSecret(Long userId, String secret);

}
