package cn.bitterfree.api.domain.login.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 11:49
 */
@Schema(description = "登录数据")
@Data
public class LoginDTO {

    @Schema(description = "邮箱登录数据")
    @Valid
    private EmailLoginDTO emailLoginDTO;

    @Schema(description = "微信登录数据")
    @Valid
    private WxLoginDTO wxLoginDTO;

    @Schema(description = "授权登录数据")
    @Valid
    private AckLoginDTO ackLoginDTO;

    @Schema(description = "密码登录数据")
    @Valid
    private PasswordLoginDTO passwordLoginDTO;

}
