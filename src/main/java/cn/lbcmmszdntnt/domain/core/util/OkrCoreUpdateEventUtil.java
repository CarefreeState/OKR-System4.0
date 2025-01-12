package cn.lbcmmszdntnt.domain.core.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.core.constants.FanoutExchangeConstants;
import cn.lbcmmszdntnt.domain.core.model.event.*;
import cn.lbcmmszdntnt.mq.sender.RabbitMQSender;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 20:44
 */
public class OkrCoreUpdateEventUtil {

    private final static int MAX_RETRIES = 3;

    private final static RabbitMQSender RABBIT_MQ_SENDER = SpringUtil.getBean(RabbitMQSender.class);

    public static void sendKeyResultUpdate(KeyResultUpdate keyResultUpdate) {
        RABBIT_MQ_SENDER.sendWithConfirm(
                FanoutExchangeConstants.KEY_RESULT_UPDATE_FANOUT,
                "",
                keyResultUpdate,
                MAX_RETRIES
        );
    }

    public static void sendTaskUpdate(TaskUpdate taskUpdate) {
        RABBIT_MQ_SENDER.sendWithConfirm(
                FanoutExchangeConstants.TASK_UPDATE_FANOUT,
                "",
                taskUpdate,
                MAX_RETRIES
        );
    }

    public static void sendStatusFlagUpdate(StatusFlagUpdate statusFlagUpdate) {
        RABBIT_MQ_SENDER.sendWithConfirm(
                FanoutExchangeConstants.STATUS_FLAG_UPDATE_FANOUT,
                "",
                statusFlagUpdate,
                MAX_RETRIES
        );
    }

    public static void sendOkrFinish(OkrFinish okrFinish) {
        RABBIT_MQ_SENDER.sendWithConfirm(
                FanoutExchangeConstants.OKR_FINISH_FANOUT,
                "",
                okrFinish,
                MAX_RETRIES
        );
    }

    public static void sendOkrInitialize(OkrInitialize okrInitialize) {
        RABBIT_MQ_SENDER.sendWithConfirm(
                FanoutExchangeConstants.OKR_INITIALIZE_FANOUT,
                "",
                okrInitialize,
                MAX_RETRIES
        );
    }

}
