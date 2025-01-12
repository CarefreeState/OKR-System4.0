package cn.lbcmmszdntnt.domain.core.model.dto;


import cn.lbcmmszdntnt.domain.core.enums.OkrType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 21:17
 */
@Schema(description = "OKR 内核带场景值的数据")
@Data
public class OkrCoreDTO {

    @Schema(description = "场景值")
    @NotNull(message = "缺少场景值")
    private OkrType scene;

    @Schema(description = "OKR 内核 ID")
    @NotNull(message = "缺少OKR 内核 ID")
    private Long coreId;

}
