package cn.lbcmmszdntnt.domain.core.util;


import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.core.loader.QuadrantDeadlineServiceLoader;
import cn.lbcmmszdntnt.domain.core.model.po.event.quadrant.FirstQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.po.event.quadrant.SecondQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.po.event.quadrant.ThirdQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.service.QuadrantDeadlineService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-23
 * Time: 12:53
 */
@Slf4j
public class QuadrantDeadlineUtil {

    private final static QuadrantDeadlineService QUADRANT_DEADLINE_SERVICE = SpringUtil.getBean(QuadrantDeadlineServiceLoader.class).load();

    public static void clear() {
        QUADRANT_DEADLINE_SERVICE.clear();
    }

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

