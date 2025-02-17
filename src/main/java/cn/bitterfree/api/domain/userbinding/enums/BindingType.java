package cn.bitterfree.api.domain.userbinding.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 21:56
 */
@Getter
@AllArgsConstructor
@Schema(
        type = "string",
        description = "绑定类型 email 邮箱绑定、wx 微信绑定",
        allowableValues = {"email", "wx"}
)
public enum BindingType {

    EMAIL("email", "邮箱绑定"),
    WX("wx", "微信绑定"),

    ;

    @JsonValue
    private final String name;
    private final String description;

}
