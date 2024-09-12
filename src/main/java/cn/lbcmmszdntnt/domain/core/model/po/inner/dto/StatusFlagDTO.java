package cn.lbcmmszdntnt.domain.core.model.po.inner.dto;

import cn.lbcmmszdntnt.common.annotation.ColorPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:23
 */
@Schema(description = "状态指标数据")
@Data
public class StatusFlagDTO {

    @Schema(name = "第四象限 ID")
    @NotNull(message = "缺少第四象限 ID")
    private Long fourthQuadrantId;

    @Schema(name = "指标内容")
    @NotBlank(message = "缺少指标内容")
    private String label;

    @Schema(name = "颜色（#十六进制）")
    @ColorPattern
    private String color;

}
