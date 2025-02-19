package cn.bitterfree.api.mq.config;

import cn.bitterfree.api.mq.client.RabbitMessageConverter;
import cn.bitterfree.api.mq.constants.DelayMessageConstants;
import cn.bitterfree.api.mq.model.entity.RabbitMQMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-18
 * Time: 16:44
 */
@Component
@Slf4j
public class RabbitMQDelayMessageConverter implements RabbitMessageConverter {

    @Override
    public <T> RabbitMQMessage<?> getRabbitMQMessage(String exchange, String routingKey, T msg, long delay, int maxRetries) {
        RabbitMQMessage<T> rabbitMQMessage = new RabbitMQMessage<>(exchange, routingKey, msg, delay, maxRetries);
        // ttl 大的排在前面（找到适合的区间，对应的 ttl 队列）
        Map.Entry<Long, String> ttlQueue = DelayMessageConstants.GLOBAL_DELAY_TTL_MAP.entrySet().stream()
                .filter(tq -> tq.getKey().compareTo(delay) < 0)
                .max(Map.Entry.comparingByKey()) // 获取 ttl 最大的
                .orElse(null);
        // queue 非空则代表进入 ttl 队列
        if(Objects.nonNull(ttlQueue)) {
            Long ttl = ttlQueue.getKey();
            String queue = ttlQueue.getValue();
            long newDelay = delay - ttl;
            log.info("由于原 delay 过长，需重新计算新的 delay {} -> {}，先进入 ttl 队列 {} {}", delay, newDelay, ttl, queue);
            rabbitMQMessage.setDelay(newDelay); // 这个 rabbitMQMessage 是未来要执行的（因为准确知道未来几时执行，所以可以预先计算新的 delay）
            return RabbitMQMessage.<RabbitMQMessage<T>>builder()
                    .exchange("")
                    .routingKey(queue)
                    .msg(rabbitMQMessage)
                    .delay(0L)
                    .maxRetries(0)
                    .build();
        } else {
            return rabbitMQMessage;
        }
    }
}
