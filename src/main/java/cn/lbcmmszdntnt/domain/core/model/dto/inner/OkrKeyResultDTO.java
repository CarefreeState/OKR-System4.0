package cn.lbcmmszdntnt.domain.core.model.dto.inner;


import cn.lbcmmszdntnt.domain.core.model.po.inner.dto.KeyResultDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
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

    @SchemaProperty(name = "场景")
    @NotBlank(message = "-> 缺少场景值")
    private String scene;

    @SchemaProperty(name = "关键结果数据")
    @NotNull(message = "-> 关键结果 为 null")
    private KeyResultDTO keyResultDTO;

}
