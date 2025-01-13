package cn.lbcmmszdntnt.domain.core.model.message.operate;

import cn.lbcmmszdntnt.domain.core.enums.TaskType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 19:12
 */
@Getter
@ToString
@Builder
public class TaskUpdate {

    private TaskType taskType;

    private Long userId;

    private Long coreId;

    private Boolean isCompleted;

    private Boolean oldCompleted;

}
