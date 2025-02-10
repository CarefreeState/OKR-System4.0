package cn.bitterfree.domain.coredeadline.handler.event;

import cn.bitterfree.domain.core.model.message.deadline.FirstQuadrantEvent;
import cn.bitterfree.domain.core.model.message.deadline.SecondQuadrantEvent;
import cn.bitterfree.domain.core.model.message.deadline.ThirdQuadrantEvent;
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
