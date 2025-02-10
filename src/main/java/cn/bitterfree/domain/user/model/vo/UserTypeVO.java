package cn.bitterfree.domain.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 18:26
 */
@Data
@Schema(description = "用户类型信息")
public class UserTypeVO {

    @Schema(description = "用户类型")
    private Integer type;

    @Schema(description = "用户类型描述")
    private String description;

}
