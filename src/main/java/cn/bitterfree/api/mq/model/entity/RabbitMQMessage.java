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

    private boolean isAvailableDelay; // 是否启动高可用延时功能

    private Long deadline; // 因为延时消息可能存入缓存，所以 delay 不可推测，所以使用 deadline 表示

}
