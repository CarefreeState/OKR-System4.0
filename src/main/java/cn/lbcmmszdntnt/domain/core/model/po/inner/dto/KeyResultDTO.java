package cn.lbcmmszdntnt.domain.core.model.po.inner.dto;

import cn.lbcmmszdntnt.common.annotation.IntRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 2:25
 */
@Schema(description = "关键结果数据")
@NotNull
@Data
public class KeyResultDTO {

    @Schema(name = "第一象限 ID")
    @NotNull(message = "第一象限 ID 不能为空")
    private Long firstQuadrantId;

    @Schema(name = "关键结果内容")
    @NotBlank(message = "关键结果内容不能为空")
    private String content;

    @Schema(name = "完成概率")
    @NotNull(message = "完成概率不能为空")
    @IntRange(min = 0, max = 100, message = "完成概率非法")
    private Integer probability;

}
