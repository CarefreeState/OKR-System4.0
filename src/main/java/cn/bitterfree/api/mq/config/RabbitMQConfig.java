package cn.bitterfree.api.mq.config;

import cn.bitterfree.api.mq.client.RabbitMQSender;
import cn.bitterfree.api.mq.model.entity.RabbitMQMessage;
import lombok.Getter;
import lombok.Setter;
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
    @ConditionalOnMissingBean(RabbitMQSender.RabbitMessageConverter.class)
    public RabbitMQSender.RabbitMessageConverter rabbitMessageConverter() {
        return RabbitMQMessage::new;
    }

}
