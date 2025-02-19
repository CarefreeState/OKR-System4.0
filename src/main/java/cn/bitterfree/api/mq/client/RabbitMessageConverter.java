package cn.bitterfree.api.mq.client;

import cn.bitterfree.api.mq.model.entity.RabbitMQMessage;

public interface RabbitMessageConverter {

        <T> RabbitMQMessage<?> getRabbitMQMessage(String exchange, String routingKey, T msg, long delay, int maxRetries);

    }