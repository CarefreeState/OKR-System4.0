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
 * Time: 1:30
 */
@Schema(description = "删除任务所需数据")
@Data
public class OkrTaskRemoveDTO {

    @Schema(description = "场景值")
    @NotNull(message = "缺少场景值")
    private OkrType scene;

    @Schema(description = "任务 ID")
    @NotNull(message = "缺少任务 ID")
    private Long id;

}
