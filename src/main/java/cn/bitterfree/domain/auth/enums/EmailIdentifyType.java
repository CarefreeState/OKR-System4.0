package cn.bitterfree.domain.auth.enums;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-13
 * Time: 11:26
 */
@Getter
@AllArgsConstructor
@Schema(
        type = "string",
        description = "邮箱验证类型 \"email-login\" 登录验证、\"email-binding\" 绑定验证",
        allowableValues = {"email-login", "email-binding"}
)
public enum EmailIdentifyType {

    LOGIN("email-login", "登录验证", GlobalServiceStatusCode.EMAIL_LOGIN_IDENTIFY_CODE_ERROR),
    BINDING("email-binding", "绑定验证", GlobalServiceStatusCode.EMAIL_BINDING_IDENTIFY_CODE_ERROR),

    ;

    @JsonValue
    private final String name;

    private final String description;

    private final GlobalServiceStatusCode errorCode;

}
