package cn.lbcmmszdntnt.domain.core.model.dto.inner;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 18:57
 */
@Schema(description = "任务更新数据")
@Data
public class TaskUpdateDTO {

    @Schema(description = "任务 ID")
    @NotNull(message = "缺少任务 ID")
    private Long id;

    @Schema(description = "任务内容")
    @NotBlank(message = "缺少任务内容")
    private String content;

    @Schema(description = "是否完成")
    @NotNull(message = "任务状态未知")
    private Boolean isCompleted;

}
