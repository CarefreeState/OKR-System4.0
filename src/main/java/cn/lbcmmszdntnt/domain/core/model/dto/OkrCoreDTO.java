package cn.lbcmmszdntnt.domain.core.model.dto;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
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
 * Time: 21:17
 */
@Schema(description = "OKR 内核带场景值的数据")
@Data
public class OkrCoreDTO {

    @SchemaProperty(name = "场景")
    private String scene;

    @SchemaProperty(name = "OKR 内核 ID")
    private Long coreId;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(coreId)) {
            messageBuilder.append("\n-> 内核 ID 为 null");
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
