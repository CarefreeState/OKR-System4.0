package cn.bitterfree.domain.okr.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-14
 * Time: 23:05
 */
@Schema(description = "Team OKR 更新数据")
@Data
public class TeamUpdateDTO {

    @Schema(description = "团队 OKR ID")
    @NotNull(message = "团队 OKR ID不能为空")
    private Long id;

    @Schema(description = "团队名")
    @NotBlank(message = "团队名不能为空")
    private String teamName;

}
