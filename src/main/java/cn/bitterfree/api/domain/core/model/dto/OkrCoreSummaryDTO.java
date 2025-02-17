package cn.bitterfree.api.domain.core.model.dto;


import cn.bitterfree.api.common.annotation.IntRange;
import cn.bitterfree.api.domain.core.enums.OkrType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-27
 * Time: 1:47
 */
@Schema(description = "总结 OKR 所需数据")
@Data
public class OkrCoreSummaryDTO {

    @Schema(description = "场景值")
    @NotNull(message = "缺少场景值")
    private OkrType scene;

    @Schema(description = "内核 ID")
    @NotNull(message = "缺少OKR 内核 ID")
    private Long coreId;

    @Schema(description = "总结的内容")
    @NotBlank(message = "总结没有内容")
    private String summary;

    @Schema(description = "完成度")
    @IntRange(min = 0, max = 300, message = "完成度非法")
    private Integer degree;

}
