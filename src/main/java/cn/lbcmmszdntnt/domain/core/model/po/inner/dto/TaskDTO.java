package cn.lbcmmszdntnt.domain.core.model.po.inner.dto;

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
 * Date: 2024-01-22
 * Time: 1:59
 */
@Schema(description = "任务数据")
@Data
public class TaskDTO {

    @SchemaProperty(name = "象限 ID")
    private Long quadrantId;

    @SchemaProperty(name = "任务内容")
    private String content;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(quadrantId)) {
            messageBuilder.append("\n-> 象限 ID 为 null");
        }
        if(!StringUtils.hasText(content)) {
            messageBuilder.append("\n-> 没有内容");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }
}
