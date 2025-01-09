package cn.lbcmmszdntnt.domain.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 18:47
 */
@Getter
@AllArgsConstructor
@Schema(
        type = "integer",
        format = "int32",
        description = "用户类型 0 封禁用户、1 普通用户、2 管理员",
        allowableValues = {"0", "1", "2"}
)
public enum UserType {

    BLOCKED_USER(0, "封禁用户"),
    NORMAL_USER(1, "普通用户"),
    MANAGER(2, "管理员"),

    ;

    @JsonValue
    @EnumValue
    private final Integer type;
    private final String description;


}
