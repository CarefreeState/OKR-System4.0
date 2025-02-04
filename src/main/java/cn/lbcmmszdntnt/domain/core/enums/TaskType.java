package cn.lbcmmszdntnt.domain.core.enums;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.exception.GlobalServiceException;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 18:21
 */
@Getter
@AllArgsConstructor
@Schema(
        type = "integer",
        format = "int32",
        description = "任务类型 0 第三象限任务、1 第二象限优先级1、2 第二象限优先级2",
        allowableValues = {"0", "1", "2"}
)
public enum TaskType {

    ACTION(0, "第三象限任务"),
    PRIORITY1(1, "第二象限优先级1"),
    PRIORITY2(2, "第二象限优先级2"),

    ;

    @JsonValue
    private final Integer option;
    private final String description;

    public static TaskType get(Integer option) {
        for(TaskType type : values()) {
            if(type.option.equals(option)) {
                return type;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.REQUEST_NOT_VALID);
    }

}
