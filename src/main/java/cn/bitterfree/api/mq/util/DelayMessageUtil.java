package cn.bitterfree.api.mq.util;

import cn.bitterfree.api.mq.constants.DelayMessageConstants;
import cn.bitterfree.api.mq.model.entity.RabbitMQMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-13
 * Time: 1:28
 */
@Slf4j
public class DelayMessageUtil {

    public static <T> RabbitMQMessage<?> getDelayMessage(String exchange, String routingKey, T msg, long delay, int maxRetries) {
        RabbitMQMessage<T> rabbitMQMessage = new RabbitMQMessage<>(exchange, routingKey, msg, delay, maxRetries);
        // ttl 大的排在前面（找到适合的区间，对应的 ttl 队列）
        Map.Entry<Long, String> ttlQueue = DelayMessageConstants.GLOBAL_DELAY_TTL_MAP.entrySet().stream()
                .filter(tq -> tq.getKey().compareTo(delay) < 0)
                .max(Map.Entry.comparingByKey()) // 获取 ttl 最大的
                .orElse(null);
        // queue 非空则代表进入 ttl 队列
        if(Objects.nonNull(ttlQueue)) {
            Long ttl = ttlQueue.getKey();
            long newDelay = delay - ttl;
            String queue = ttlQueue.getValue();
            log.info("由于原 delay 过长，需重新计算新的 delay {} -> {}，先进入 ttl 队列 {} {}", delay, newDelay, ttl, queue);
            rabbitMQMessage.setDelay(newDelay);
            return RabbitMQMessage.<RabbitMQMessage<T>>builder()
                    .exchange("")
                    .routingKey(queue)
                    .msg(rabbitMQMessage)
                    .delay(0L)
                    .maxRetries(maxRetries)
                    .build();
        } else {
            return rabbitMQMessage;
        }
    }

}
