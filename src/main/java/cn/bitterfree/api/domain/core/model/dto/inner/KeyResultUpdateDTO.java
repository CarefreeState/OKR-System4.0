package cn.bitterfree.api.domain.core.model.dto.inner;

import cn.bitterfree.api.common.annotation.IntRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 22:54
 */
@Schema(description = "关键结果更新数据")
@Data
public class KeyResultUpdateDTO {

    @Schema(description = "关键结果 ID")
    @NotNull(message = "关键结果 ID 不能为空")
    private Long id;

    @Schema(description = "完成概率")
    @NotNull(message = "完成概率不能为空")
    @IntRange(min = 0, max = 100, message = "完成概率非法")
    private Integer probability;

}
