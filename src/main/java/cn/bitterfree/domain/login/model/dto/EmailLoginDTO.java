package cn.bitterfree.domain.login.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 11:54
 */
@Schema(description = "邮箱登录数据")
@Data
public class EmailLoginDTO {

    @Schema(description = "code")
    @NotBlank(message = "code 不能为空")
    private String code;

    @Schema(description = "email")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不合法")
    private String email;

}
