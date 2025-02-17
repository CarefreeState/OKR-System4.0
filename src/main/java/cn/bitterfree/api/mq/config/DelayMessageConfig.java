package cn.bitterfree.api.mq.config;

import cn.bitterfree.api.mq.sender.RabbitMessageConverter;
import cn.bitterfree.api.mq.util.DelayMessageUtil;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cn.bitterfree.api.mq.constants.DelayMessageConstants.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-12
 * Time: 22:42
 */
@Configuration
public class DelayMessageConfig {

    private Queue getTtlQueue(String queue, long ttl) {
        return QueueBuilder.durable(queue)
                .ttl((int) ttl)
                .lazy()
                .deadLetterExchange(GLOBAL_DELAY_DIRECT)
                .deadLetterRoutingKey(GLOBAL_DELAY_QUEUE)
                .build();
    }

    @Bean
    public Queue _12DaysTtlQueue() {
        return getTtlQueue(GLOBAL_12_DAYS_TTL_QUEUE, GLOBAL_12_DAYS_TTL);
    }

    @Bean
    public Queue _24DaysTtlQueue() {
        return getTtlQueue(GLOBAL_24_DAYS_TTL_QUEUE, GLOBAL_24_DAYS_TTL);
    }

    @Bean
    public RabbitMessageConverter rabbitMessageConverter() {
//        return RabbitMQMessage::new;
        return DelayMessageUtil::getDelayMessage;
    }

}
