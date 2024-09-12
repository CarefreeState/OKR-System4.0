package cn.lbcmmszdntnt.domain.core.model.dto.inner;


import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.TaskDTO;
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
 * Time: 1:13
 */
@Schema(description = "增加任务所需数据")
@Data
public class OkrTaskDTO {

    @Schema(name = "场景")
    @NotBlank(message = "缺少场景值")
    private String scene;

    @Schema
    @NotNull(message = "缺少任务数据")
    @Valid
    private TaskDTO taskDTO;

}
