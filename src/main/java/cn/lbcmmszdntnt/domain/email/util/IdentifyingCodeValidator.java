package cn.lbcmmszdntnt.domain.email.util;

import cn.hutool.core.util.RandomUtil;

public class IdentifyingCodeValidator {

    public static final int IDENTIFYING_CODE_SIZE = 6; // 验证码长度

    public static final String IDENTIFYING_CODE = "IdentifyingCode"; // 验证码

    public static final String IDENTIFYING_OPPORTUNITIES = "IdentifyingOpportunities"; // 剩余验证验证机会

    public static final String REDIS_EMAIL_CODE = "redisEmailCode:%s:%s";

    public static final String EMAIL_LOGIN = "email-login";

    public static final String EMAIL_BINDING = "email-binding";

    public static String getIdentifyingCode() {
        return RandomUtil.randomNumbers(IDENTIFYING_CODE_SIZE);
    }

}
