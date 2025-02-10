package cn.bitterfree.domain.auth.model.dto;

import cn.bitterfree.domain.qrcode.enums.QRCodeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 16:28
 */
@Data
@Schema(description = "登录码数据")
public class LoginQRCodeDTO {

    @Schema(description = "登录码类型")
    @NotNull(message = "登录码类型不能为空")
    private QRCodeType type;

}
