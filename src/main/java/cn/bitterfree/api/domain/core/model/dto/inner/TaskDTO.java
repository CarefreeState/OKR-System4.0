package cn.bitterfree.api.domain.core.model.dto.inner;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 1:59
 */
@Schema(description = "任务数据")
@Data
public class TaskDTO {

    @Schema(description = "象限 ID")
    @NotNull(message = "缺少象限 ID")
    private Long quadrantId;

    @Schema(description = "任务内容")
    @NotBlank(message = "缺少任务内容")
    private String content;

}
