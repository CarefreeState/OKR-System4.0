package cn.lbcmmszdntnt.domain.quadrantdeadline.service;


import cn.lbcmmszdntnt.domain.quadrantdeadline.model.event.FirstQuadrantEvent;
import cn.lbcmmszdntnt.domain.quadrantdeadline.model.event.SecondQuadrantEvent;
import cn.lbcmmszdntnt.domain.quadrantdeadline.model.event.ThirdQuadrantEvent;

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