package cn.lbcmmszdntnt.domain.core.model.po.inner.dto;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.po.inner.StatusFlag;
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
 * Time: 17:33
 */
@Schema(description = "状态指标更新数据")
@Data
public class StatusFlagUpdateDTO {

    @SchemaProperty(name = "指标 ID")
    private Long id;

    @SchemaProperty(name = "指标内容")
    private String label;

    @SchemaProperty(name = "颜色（#十六进制）")
    private String color;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("\n-> 指标 ID 为 null");
        }
        if(!StringUtils.hasText(label)) {
            messageBuilder.append("\n-> 指标为空");
        }
        if(!StringUtils.hasText(color) || !color.matches(StatusFlag.COLOR_PATTERN)) {
            messageBuilder.append("\n-> 颜色非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
