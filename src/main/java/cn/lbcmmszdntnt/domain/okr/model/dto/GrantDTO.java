package cn.lbcmmszdntnt.domain.okr.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 1:43
 */
@Schema(description = "授权成员所需数据")
@Data
public class GrantDTO {

    @Schema(description = "团队 OKR ID")
    @NotNull(message = "团队 OKR ID不能为空")
    private Long teamId;

    @Schema(description = "用户 ID")
    @NotNull(message = "用户 ID不能为空")
    private Long userId;

    @Schema(description = "团队名")
    @NotBlank(message = "团队名不能为空")
    private String teamName;

}
