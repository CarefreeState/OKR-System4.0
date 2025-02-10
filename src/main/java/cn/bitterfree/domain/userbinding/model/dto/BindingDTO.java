package cn.bitterfree.domain.userbinding.model.dto;

import cn.bitterfree.domain.userbinding.enums.BindingType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 1:25
 */
@Data
@Schema(description = "绑定数据")
public class BindingDTO {

    @Schema(description = "绑定类型")
    @NotNull(message = "绑定类型不能为空")
    private BindingType type;

    @Schema(description = "邮箱绑定数据")
    @Valid
    private EmailBindingDTO emailBindingDTO;

    @Schema(description = "微信绑定数据")
    @Valid
    private WxBindingDTO wxBindingDTO;

}
