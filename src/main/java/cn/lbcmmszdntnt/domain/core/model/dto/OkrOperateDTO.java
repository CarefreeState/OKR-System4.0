package cn.lbcmmszdntnt.domain.core.model.dto;


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

    @Schema(description = "场景")
    @NotNull(message = "缺少场景值")
    private String scene;

    @Schema(description = "团队 OKR ID", nullable = true)
    private Long teamOkrId;

    @Schema(description = "邀请密钥", nullable = true)
    private String secret;

    @Schema(description = "邀请码类型", nullable = true)
    private String type;

    @Schema(description = "团队名", nullable = true)
    private String teamName;

}
