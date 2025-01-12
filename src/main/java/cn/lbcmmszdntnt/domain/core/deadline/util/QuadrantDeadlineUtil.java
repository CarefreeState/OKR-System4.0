package cn.lbcmmszdntnt.domain.core.deadline.util;


import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.core.model.entity.event.quadrant.FirstQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.entity.event.quadrant.SecondQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.entity.event.quadrant.ThirdQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.service.QuadrantDeadlineService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-23
 * Time: 12:53
 */
// 平时调用此类的方法，并不会阻塞，只有在系统的象限截止时间刷新期间，无法通过这个类提交任务（）
@Slf4j
public class QuadrantDeadlineUtil {

    private final static QuadrantDeadlineService QUADRANT_DEADLINE_SERVICE = SpringUtil.getBean(QuadrantDeadlineService.class);

    public static void scheduledComplete(FirstQuadrantEvent firstQuadrantEvent) {
        QUADRANT_DEADLINE_SERVICE.scheduledComplete(firstQuadrantEvent);
    }

    public static void scheduledUpdateSecondQuadrant(SecondQuadrantEvent secondQuadrantEvent) {
        QUADRANT_DEADLINE_SERVICE.scheduledUpdateSecondQuadrant(secondQuadrantEvent);
    }

    public static void scheduledUpdateThirdQuadrant(ThirdQuadrantEvent thirdQuadrantEvent) {
        QUADRANT_DEADLINE_SERVICE.scheduledUpdateThirdQuadrant(thirdQuadrantEvent);
    }

}

