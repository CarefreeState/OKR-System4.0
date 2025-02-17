package cn.bitterfree.api.domain.core.model.dto.inner;


import cn.bitterfree.api.domain.core.enums.OkrType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-27
 * Time: 0:58
 */
@Schema(description = "删除状态指标所需数据")
@Data
public class OkrStatusFlagRemoveDTO {

    @Schema(description = "场景值")
    @NotNull(message = "缺少场景值")
    private OkrType scene;

    @Schema(description = "状态指标 ID")
    @NotNull(message = "缺少状态指标 ID")
    private Long id;

}
