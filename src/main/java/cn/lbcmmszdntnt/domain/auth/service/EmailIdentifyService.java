package cn.lbcmmszdntnt.domain.auth.service;


import cn.lbcmmszdntnt.domain.auth.enums.EmailIdentifyType;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-12
 * Time: 16:41
 */
public interface EmailIdentifyService {

    String sendIdentifyingCode(EmailIdentifyType emailIdentifyType, String email);

    void validateEmailCode(EmailIdentifyType emailIdentifyType, String email, String code);

}
