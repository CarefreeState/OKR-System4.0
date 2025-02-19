package cn.bitterfree.api.mq.config;

import cn.bitterfree.api.common.util.convert.JsonUtil;
import cn.bitterfree.api.mq.client.RabbitMessageConverter;
import cn.bitterfree.api.mq.model.entity.RabbitMQMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-18
 * Time: 15:14
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.mq")
public class RabbitMQConfig {

    private String host;

    private String port;

    private String httpApi;

    private String username;

    private String password;

    private String virtualHost;

    @Bean
    @ConditionalOnMissingBean(RabbitMessageConverter.class)
    public RabbitMessageConverter rabbitMessageConverter() {
        return RabbitMQMessage::new;
    }

    @Bean
    public MessageConverter messageConverter(){
        // 1. 定义消息转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(JsonUtil.OBJECT_MAPPER);
        // 2. 配置自动创建消息 id，用于识别不同消息
        jackson2JsonMessageConverter.setCreateMessageIds(Boolean.TRUE);
        return jackson2JsonMessageConverter;
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RejectAndDontRequeueRecoverer(); // nack、直接 reject 和不 requeue，成为死信（默认）
//        return new ImmediateRequeueMessageRecoverer(); // nack、requeue
//        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error"); // ack、发送给指定的交换机，confirm 机制需要设置到 rabbitTemplate 里
    }

}
