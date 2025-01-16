package cn.lbcmmszdntnt.domain.userbinding.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 20:28
 */
@Data
@Schema(description = "邮箱绑定数据")
public class EmailBindingDTO {

    @Schema(description = "code")
    @NotBlank(message = "code 不能为空")
    private String code;

    @Schema(description = "email")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不合法")
    private String email;

}
