package cn.bitterfree.api.domain.core.util;


import cn.bitterfree.api.domain.core.model.message.deadline.FirstQuadrantEvent;
import cn.bitterfree.api.domain.core.model.message.deadline.SecondQuadrantEvent;
import cn.bitterfree.api.domain.core.model.message.deadline.ThirdQuadrantEvent;
import cn.bitterfree.api.mq.client.RabbitMQSender;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import static cn.bitterfree.api.domain.core.constants.DelayExchangeConstants.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-23
 * Time: 12:53
 */
// 平时调用此类的方法，并不会阻塞，只有在系统的象限截止时间刷新期间，无法通过这个类提交任务（）
@Slf4j
public class QuadrantDeadlineMessageUtil {

    private final static int MAX_RETRIES = 3;

    private final static RabbitMQSender RABBIT_MQ_SENDER = SpringUtil.getBean(RabbitMQSender.class);

    public static void scheduledComplete(FirstQuadrantEvent firstQuadrantEvent) {
        RABBIT_MQ_SENDER.sendDelayMessageWithConfirm(
                QUADRANT_DDL_DELAY_DIRECT,
                FIRST_DDL,
                firstQuadrantEvent,
                firstQuadrantEvent.getDeadline().getTime() - System.currentTimeMillis(),
                MAX_RETRIES
        );
    }

    public static void scheduledUpdateSecondQuadrant(SecondQuadrantEvent secondQuadrantEvent) {
        RABBIT_MQ_SENDER.sendDelayMessageWithConfirm(
                QUADRANT_DDL_DELAY_DIRECT,
                SECOND_DDL,
                secondQuadrantEvent,
                secondQuadrantEvent.getDeadline().getTime() - System.currentTimeMillis(),
                MAX_RETRIES
        );
    }

    public static void scheduledUpdateThirdQuadrant(ThirdQuadrantEvent thirdQuadrantEvent) {
        RABBIT_MQ_SENDER.sendDelayMessageWithConfirm(
                QUADRANT_DDL_DELAY_DIRECT,
                THIRD_DDL,
                thirdQuadrantEvent,
                thirdQuadrantEvent.getDeadline().getTime() - System.currentTimeMillis(),
                MAX_RETRIES
        );
    }

}

