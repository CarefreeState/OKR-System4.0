package cn.bitterfree.api.mq.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-12
 * Time: 22:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RabbitMQMessage<T> {

    private String exchange;

    private String routingKey;

    private T msg;

    private long delay;

    private int maxRetries;

}
