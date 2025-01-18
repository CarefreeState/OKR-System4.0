package cn.lbcmmszdntnt.domain.core.model.vo;

import cn.lbcmmszdntnt.common.util.convert.DateTimeUtil;
import cn.lbcmmszdntnt.domain.core.model.entity.inner.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-18
 * Time: 18:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OkrNoticeTemplateVO {

    private String nickname;

    private String objective;
    private List<KeyResult> keyResultList;

    private List<PriorityNumberOne> priorityOneList;
    private List<PriorityNumberTwo> priorityTwoList;

    private List<Action> actionList;

    private List<StatusFlag> statusList;

    private Date nextDeadline;

    public String getNextDeadline() {
        return DateTimeUtil.getDateFormat(nextDeadline);
    }
}
