package cn.lbcmmszdntnt.domain.core.service.impl;

import cn.lbcmmszdntnt.domain.core.model.event.deadline.FirstQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.event.deadline.SecondQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.event.deadline.ThirdQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.service.QuadrantDeadlineService;
import cn.lbcmmszdntnt.mq.sender.RabbitMQSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.lbcmmszdntnt.domain.core.constants.DelayExchangeConstants.*;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-10
 * Time: 2:06
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuadrantDeadlineServiceImpl implements QuadrantDeadlineService {

    private final static int MAX_RETRIES = 3;

    private final RabbitMQSender rabbitMQSender;

    @Override
    public void scheduledComplete(FirstQuadrantEvent firstQuadrantEvent) {
        rabbitMQSender.sendDelayMessageWithConfirm(
                QUADRANT_DDL_DELAY_DIRECT,
                FIRST_DDL,
                firstQuadrantEvent,
                firstQuadrantEvent.getDeadline().getTime() - System.currentTimeMillis(),
                MAX_RETRIES
        );
    }

    @Override
    public void scheduledUpdateSecondQuadrant(SecondQuadrantEvent secondQuadrantEvent) {
        rabbitMQSender.sendDelayMessageWithConfirm(
                QUADRANT_DDL_DELAY_DIRECT,
                SECOND_DDL,
                secondQuadrantEvent,
                secondQuadrantEvent.getDeadline().getTime() - System.currentTimeMillis(),
                MAX_RETRIES
        );
    }

    @Override
    public void scheduledUpdateThirdQuadrant(ThirdQuadrantEvent thirdQuadrantEvent) {
        rabbitMQSender.sendDelayMessageWithConfirm(
                QUADRANT_DDL_DELAY_DIRECT,
                THIRD_DDL,
                thirdQuadrantEvent,
                thirdQuadrantEvent.getDeadline().getTime() - System.currentTimeMillis(),
                MAX_RETRIES
        );
    }
}
