package cn.lbcmmszdntnt.domain.auth.enums;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-13
 * Time: 11:28
 */
@Getter
@AllArgsConstructor
@Schema(
        type = "string",
        description = "登录类型 Rl0p0r 邮箱登录、r6Vsr0 微信登录、Z-1_rf 授权登录",
        allowableValues = {"Rl0p0r", "r6Vsr0", "Z-1_rf"}
)
public enum LoginType {

    EMAIL("Rl0p0r", "邮箱登录"),
    WX("r6Vsr0", "微信登录"),
    ACK("Z-1_rf", "授权登录"),

    ;

    @JsonValue
    private final String name;

    private final String description;

    public static LoginType get(String name) {
        for(LoginType type : values()) {
            if(type.name.equals(name)) {
                return type;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.REQUEST_NOT_VALID);
    }

}
