package cn.lbcmmszdntnt.domain.email.service;

public interface EmailService {

    /**
     * 向用户邮箱发送验证码
     *
     * @param type 验证类型
     * @param email 用户的邮箱
     * @param code  验证码
     */
    void sendIdentifyingCode(String type, String email, String code);

    /**
     * 校验当前邮箱用户输入的验证码是否正确
     *
     * @param type 验证类型
     * @param email 用户的邮箱
     * @param code  验证码
     */
    void checkIdentifyingCode(String type, String email, String code);

}
