package cn.bitterfree.mq.listener;

import cn.bitterfree.mq.constants.DelayMessageConstants;
import cn.bitterfree.mq.model.entity.RabbitMQMessage;
import cn.bitterfree.mq.sender.RabbitMQSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

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

    @RabbitListener(queuesToDeclare = @Queue(name = DelayMessageConstants.GLOBAL_DELAY_QUEUE))
    public void delayMessageRepublish(RabbitMQMessage<?> rabbitMQMessage) {
        Long delay = rabbitMQMessage.getDelay();
        String exchange = rabbitMQMessage.getExchange();
        log.info("新延时时间为 {} 的消息，重新尝试发送给延时交换机 {}", delay, exchange);
        rabbitMQSender.send(
                exchange,
                rabbitMQMessage.getRoutingKey(),
                rabbitMQMessage.getMsg(),
                delay,
                rabbitMQMessage.getMaxRetries()
        );
    }

}
