package cn.lbcmmszdntnt.domain.core.service;


import cn.lbcmmszdntnt.domain.core.model.event.deadline.FirstQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.event.deadline.SecondQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.event.deadline.ThirdQuadrantEvent;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-12
 * Time: 18:42
 */
public interface QuadrantDeadlineService {

    void scheduledComplete(FirstQuadrantEvent firstQuadrantEvent);

    void scheduledUpdateSecondQuadrant(SecondQuadrantEvent secondQuadrantEvent);

    void scheduledUpdateThirdQuadrant(ThirdQuadrantEvent thirdQuadrantEvent);

}