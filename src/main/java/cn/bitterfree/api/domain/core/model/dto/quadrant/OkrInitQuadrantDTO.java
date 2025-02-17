package cn.bitterfree.api.domain.core.model.dto.quadrant;


import cn.bitterfree.api.domain.core.enums.OkrType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 23:21
 */
@Schema(description = "初始化二三象限所需数据")
@Data
public class OkrInitQuadrantDTO {

    @Schema(description = "场景值")
    @NotNull(message = "缺少场景值")
    private OkrType scene;

    @Schema
    @NotNull(message = "缺少初始化象限数据")
    @Valid
    private InitQuadrantDTO initQuadrantDTO;

}
