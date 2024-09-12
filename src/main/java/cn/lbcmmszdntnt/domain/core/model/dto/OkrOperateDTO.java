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

    @Schema(name = "场景")
    @NotNull(message = "缺少场景值")
    private String scene;

    @Schema(name = "团队 OKR ID")
    private Long teamOkrId;

    @Schema(name = "邀请密钥")
    private String secret;

    @Schema(name = "邀请码类型")
    private String type;

    @Schema(name = "团队名")
    private String teamName;

}
