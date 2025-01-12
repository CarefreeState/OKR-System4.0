package cn.lbcmmszdntnt.domain.core.handler.event;

import cn.lbcmmszdntnt.domain.core.model.event.deadline.FirstQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.event.deadline.SecondQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.event.deadline.ThirdQuadrantEvent;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-23
 * Time: 12:05
 */
@Data
public class DeadlineEvent {

    private FirstQuadrantEvent firstQuadrantEvent;

    private SecondQuadrantEvent secondQuadrantEvent;

    private ThirdQuadrantEvent thirdQuadrantEvent;

}
