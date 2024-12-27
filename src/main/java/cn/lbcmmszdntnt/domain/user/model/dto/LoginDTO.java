package cn.lbcmmszdntnt.domain.user.model.dto;

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

    @Schema
    @Valid
    private EmailLoginDTO emailLoginDTO;

    @Schema
    @Valid
    private WxLoginDTO wxLoginDTO;

}
