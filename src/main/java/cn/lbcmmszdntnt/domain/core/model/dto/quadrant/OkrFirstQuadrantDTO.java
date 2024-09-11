package cn.lbcmmszdntnt.domain.core.model.dto.quadrant;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.dto.FirstQuadrantDTO;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 23:05
 */
@Schema(description = "第一象限修改所需数据")
@Data
public class OkrFirstQuadrantDTO {

    @SchemaProperty(name = "场景")
    private String scene;

    @SchemaProperty(name = "第一象限 数据")
    private FirstQuadrantDTO firstQuadrantDTO;


    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(firstQuadrantDTO)) {
            messageBuilder.append("\n-> 第一象限 为 null");
        }
        if(!StringUtils.hasText(scene)) {
            messageBuilder.append("\n-> 缺少场景值");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
