package cn.lbcmmszdntnt.domain.core.model.dto.quadrant;


import cn.lbcmmszdntnt.domain.core.model.po.quadrant.dto.InitQuadrantDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 23:21
 */
@Schema(description = "初始化二三象限所需数据")
@Data
public class OkrInitQuadrantDTO {

    @Schema(name = "场景")
    @NotBlank(message = "缺少场景值")
    private String scene;

    @Schema
    @NotNull(message = "缺少初始化象限数据")
    @Valid
    private InitQuadrantDTO initQuadrantDTO;

}
