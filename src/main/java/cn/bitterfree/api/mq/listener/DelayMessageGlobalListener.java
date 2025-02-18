package cn.bitterfree.api.mq.listener;

import cn.bitterfree.api.common.util.juc.threadpool.SchedulerThreadPool;
import cn.bitterfree.api.mq.client.RabbitMQSender;
import cn.bitterfree.api.mq.constants.DelayMessageConstants;
import cn.bitterfree.api.mq.model.entity.RabbitMQMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-12
 * Time: 22:54
 */
@Component
@RequiredArgsConstructor
@Slf4j
@DependsOn("delayMessageConfig") // 保证在 DelayMessageConfig 加载之后
public class DelayMessageGlobalListener {

    private final RabbitMQSender rabbitMQSender;

    @RabbitListener(queuesToDeclare = @Queue(name = DelayMessageConstants.LOCAL_DELAY_QUEUE))
    public void localDelayMessage(RabbitMQMessage<?> rabbitMQMessage) {
        SchedulerThreadPool.schedule(() -> {
            rabbitMQSender.sendWithConfirm(
                    rabbitMQMessage.getExchange(),
                    rabbitMQMessage.getRoutingKey(),
                    rabbitMQMessage.getMsg(),
                    rabbitMQMessage.getMaxRetries()
            );
        }, rabbitMQMessage.getDelay(), TimeUnit.MILLISECONDS);
    }

    @RabbitListener(queuesToDeclare = @Queue(name = DelayMessageConstants.GLOBAL_DELAY_QUEUE))
    public void delayMessageRepublish(RabbitMQMessage<?> rabbitMQMessage) {
        rabbitMQSender.sendDelayMessageWithConfirm(
                rabbitMQMessage.getExchange(),
                rabbitMQMessage.getRoutingKey(),
                rabbitMQMessage.getMsg(),
                rabbitMQMessage.getDelay(),
                rabbitMQMessage.getMaxRetries()
        );
    }

}
