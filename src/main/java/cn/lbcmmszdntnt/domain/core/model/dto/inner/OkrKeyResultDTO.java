package cn.lbcmmszdntnt.domain.core.model.dto.inner;


import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.KeyResultDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-27
 * Time: 0:31
 */
@Schema(description = "添加关键结果所需数据")
@Data
public class OkrKeyResultDTO {

    @Schema(description = "场景值")
    @NotBlank(message = "缺少场景值")
    private String scene;

    @Schema
    @NotNull(message = "缺少关键结果")
    @Valid
    private KeyResultDTO keyResultDTO;

}
