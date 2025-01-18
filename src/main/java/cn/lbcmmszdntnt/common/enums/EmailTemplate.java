package cn.lbcmmszdntnt.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplate {

    EMAIL_IDENTIFY("邮箱验证", "identifying-code-model.html"),

    SHORT_TERM_NOTICE("OKR 短期计划周期提醒", "short-term-notice-model.html"),
    LONG_TERM_NOTICE("OKR 长期计划周期提醒", "long-term-notice-model.html"),
    OKR_ENDED_NOTICE("OKR 结束提醒", "okr-ended-notice-model.html"),

    ;

    /**
     * 本次邮件拟定标题
     */
    private final String title;
    /**
     * 本次邮件格式模板
     */
    private final String template;

}
