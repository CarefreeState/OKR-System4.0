package cn.bitterfree.mq.constants;

import cn.bitterfree.mq.model.entity.RabbitMQMessage;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-12
 * Time: 22:56
 */
public interface DelayMessageConstants {

    long GLOBAL_12_DAYS_TTL = TimeUnit.DAYS.toMillis(12);
    long GLOBAL_24_DAYS_TTL = TimeUnit.DAYS.toMillis(24);
    String GLOBAL_12_DAYS_TTL_QUEUE = "global.12days.ttl.queue";
    String GLOBAL_24_DAYS_TTL_QUEUE = "global.24days.ttl.queue";
    Map<Long, String> GLOBAL_DELAY_TTL_MAP = Map.of(
            GLOBAL_12_DAYS_TTL, GLOBAL_12_DAYS_TTL_QUEUE,
            GLOBAL_24_DAYS_TTL, GLOBAL_24_DAYS_TTL_QUEUE
    );

    String GLOBAL_DELAY_DIRECT = "";
    String GLOBAL_DELAY_QUEUE = "global.delay.queue";

    static <T> RabbitMQMessage<?> getDelayMessage(String exchange, String routingKey, T msg, long delay, int maxRetries) {
        RabbitMQMessage<T> rabbitMQMessage = new RabbitMQMessage<>(exchange, routingKey, msg, delay, maxRetries);
        // ttl 大的排在前面（找到适合的区间，对应的 ttl 队列）
        Map.Entry<Long, String> queue = GLOBAL_DELAY_TTL_MAP.entrySet().stream()
                .filter(ttlQueue -> ttlQueue.getKey().compareTo(delay) < 0)
                .max(Map.Entry.comparingByKey()) // 获取 ttl 最大的
                .orElse(null);
        // queue 非空则代表进入 ttl 队列
        if(Objects.nonNull(queue)) {
            // 计算新的 delay
            rabbitMQMessage.setDelay(delay - queue.getKey());
            return RabbitMQMessage.<RabbitMQMessage<T>>builder()
                    .exchange("")
                    .routingKey(queue.getValue())
                    .msg(rabbitMQMessage)
                    .delay(0L)
                    .maxRetries(maxRetries)
                    .build();
        } else {
            return rabbitMQMessage;
        }
    }

}
