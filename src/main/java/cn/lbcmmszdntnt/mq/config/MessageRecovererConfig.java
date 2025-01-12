package cn.lbcmmszdntnt.mq.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.ImmediateRequeueMessageRecoverer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 13:34
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MessageRecovererConfig {

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RejectAndDontRequeueRecoverer(); // nack、直接 reject 和不 requeue，成为死信（默认）
//        return new ImmediateRequeueMessageRecoverer(); // nack、requeue
//        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error"); // ack、发送给指定的交换机
    }


}
