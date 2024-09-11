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
 * Time: 18:57
 */
@Schema(description = "任务更新数据")
@Data
public class TaskUpdateDTO {

    @SchemaProperty(name = "任务 ID")
    private Long id;

    @SchemaProperty(name = "任务内容")
    private String content;

    @SchemaProperty(name = "是否完成")
    private Boolean isCompleted;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("\n-> 任务 ID 为 null");
        }
        if(!StringUtils.hasText(content)) {
            messageBuilder.append("\n-> 没有内容");
        }
        if(Objects.isNull(isCompleted)) {
            messageBuilder.append("\n-> 任务状态未知");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
