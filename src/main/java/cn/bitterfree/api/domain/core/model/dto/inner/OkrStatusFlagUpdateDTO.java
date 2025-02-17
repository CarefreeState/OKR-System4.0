package cn.bitterfree.api.domain.core.model.dto.inner;


import cn.bitterfree.api.domain.core.enums.OkrType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-27
 * Time: 1:03
 */
@Schema(description = "更新状态指标所需数据")
@Data
public class OkrStatusFlagUpdateDTO {

    @Schema(description = "场景值")
    @NotNull(message = "缺少场景值")
    private OkrType scene;

    @Schema
    @NotNull(message = "缺少更新状态指标的数据")
    @Valid
    private StatusFlagUpdateDTO statusFlagUpdateDTO;

}
