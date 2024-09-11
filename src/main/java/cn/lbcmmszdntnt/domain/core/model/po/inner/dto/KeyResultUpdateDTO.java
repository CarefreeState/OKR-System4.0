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
 * Date: 2024-01-21
 * Time: 22:54
 */
@Schema(description = "关键结果更新数据")
@Data
public class KeyResultUpdateDTO {

    @SchemaProperty(name = "关键结果 ID")
    private Long id;

    @SchemaProperty(name = "完成概率")
    private Integer probability;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("\n-> 关键结果 ID 为 null");
        }
        if(Objects.isNull(probability) ||
                probability.compareTo(0) < 0 || probability.compareTo(100) > 0) {
            messageBuilder.append("\n-> 完成概率非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
