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
 * Date: 2024-01-27
 * Time: 1:47
 */
@Schema(description = "总结 OKR 所需数据")
@Data
public class OkrCoreSummaryDTO {

    private final static Integer MAX_DEGREE = 300;

    @SchemaProperty(name = "场景")
    private String scene;

    @SchemaProperty(name = "内核 ID")
    private Long coreId;

    @SchemaProperty(name = "总结的内容")
    private String summary;

    @SchemaProperty(name = "完成度")
    private Integer degree;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(coreId)) {
            messageBuilder.append("\n-> 内核 ID 为 null");
        }
        if(!StringUtils.hasText(scene)) {
            messageBuilder.append("\n-> 缺少场景值");
        }
        if(!StringUtils.hasText(summary)) {
            messageBuilder.append("\n-> 总结没有内容");
        }
        if(Objects.isNull(degree) || degree.compareTo(0) < 0 || degree.compareTo(MAX_DEGREE) > 0) {
            messageBuilder.append("\n-> 完成度非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
