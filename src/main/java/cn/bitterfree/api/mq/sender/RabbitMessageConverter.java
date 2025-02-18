package cn.bitterfree.api.mq.sender;

import cn.bitterfree.api.mq.model.entity.RabbitMQMessage;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-13
 * Time: 0:02
 */
public interface RabbitMessageConverter {

    <T> RabbitMQMessage<?> getRabbitMQMessage(String exchange, String routingKey, T msg, long delay, int maxRetries, boolean isAvailableDelay);

}
