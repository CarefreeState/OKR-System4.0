package cn.lbcmmszdntnt.domain.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 18:20
 */
@Getter
@AllArgsConstructor
@Schema(
        type = "string",
        description = "OKR 类型 scene-p 个人 OKR、scene-t 团队 OKR、scene-tp 团队个人 OKR",
        allowableValues = {"scene-p", "scene-t", "scene-tp"}
)
public enum OkrType {

    PERSONAL("scene-p", "个人 OKR"),
    TEAM("scene-t", "团队 OKR"),
    TEAM_PERSONAL("scene-tp", "团队个人 OKR"),

    ;

    @JsonValue
    private final String scene;
    private final String description;

}
