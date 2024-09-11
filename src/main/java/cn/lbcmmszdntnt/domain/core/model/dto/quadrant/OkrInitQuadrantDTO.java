package cn.lbcmmszdntnt.domain.core.model.dto.quadrant;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.dto.InitQuadrantDTO;
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
 * Time: 23:21
 */
@Schema(description = "初始化二三象限所需数据")
@Data
public class OkrInitQuadrantDTO {

    @SchemaProperty(name = "场景")
    private String scene;

    @SchemaProperty(name = "初始化象限数据")
    private InitQuadrantDTO initQuadrantDTO;


    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(initQuadrantDTO)) {
            messageBuilder.append("\n-> 初始化象限数据 为 null");
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
