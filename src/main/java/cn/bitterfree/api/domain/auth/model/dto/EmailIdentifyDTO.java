package cn.bitterfree.api.domain.auth.model.dto;

import cn.bitterfree.api.domain.auth.enums.EmailIdentifyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 14:26
 */
@Schema(description = "邮箱验证数据")
@Data
public class EmailIdentifyDTO {

    @Schema(description = "验证类型")
    @NotNull(message = "验证类型不能为空")
    private EmailIdentifyType type;

    @Schema(description = "email")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不合法")
    private String email;

}
