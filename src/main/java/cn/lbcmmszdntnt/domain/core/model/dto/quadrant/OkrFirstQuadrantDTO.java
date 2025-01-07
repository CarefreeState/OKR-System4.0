package cn.lbcmmszdntnt.domain.core.model.dto.quadrant;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 23:05
 */
@Schema(description = "第一象限修改所需数据")
@Data
public class OkrFirstQuadrantDTO {

    @Schema(description = "场景")
    @NotBlank(message = "缺少场景值")
    private String scene;

    @Schema
    @NotNull(message = "缺少第一象限数据")
    @Valid
    private FirstQuadrantDTO firstQuadrantDTO;

}
