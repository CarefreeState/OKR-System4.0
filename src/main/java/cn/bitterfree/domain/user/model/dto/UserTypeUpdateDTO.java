package cn.bitterfree.domain.user.model.dto;

import cn.bitterfree.domain.user.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-17
 * Time: 20:31
 */
@Data
@Schema(description = "用户状态更新数据")
public class UserTypeUpdateDTO {

    @Schema(description = "用户类型")
    @NotNull(message = "用户类型不能为空")
    private UserType userType;

}
