package cn.bitterfree.api.domain.login.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 17:34
 */
@Data
public class AckLoginDTO {

    @Schema(description = "登录密钥")
    @NotBlank(message = "登录密钥不能为空")
    private String secret;

}
