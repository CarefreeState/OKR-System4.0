package cn.bitterfree.domain.core.model.dto;


import cn.bitterfree.domain.core.enums.OkrType;
import cn.bitterfree.domain.qrcode.enums.QRCodeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 19:18
 */
@Schema(description = "OKR 操作数据")
@Data
public class OkrOperateDTO {

    @Schema(description = "场景值")
    @NotNull(message = "缺少场景值")
    private OkrType scene;

    @Schema(description = "团队 OKR ID", nullable = true)
    private Long teamOkrId;

    @Schema(description = "邀请密钥", nullable = true)
    private String secret;

    @Schema(description = "邀请码类型", nullable = true)
    private QRCodeType type;

    @Schema(description = "团队名", nullable = true)
    private String teamName;

}
