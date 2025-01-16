package cn.lbcmmszdntnt.domain.userbinding.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "微信绑定数据")
public class WxBindingDTO {

    @Schema(description = "绑定密钥")
    @NotBlank(message = "绑定密钥不能为空")
    private String secret;

}
