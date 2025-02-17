package cn.bitterfree.api.mq.config;

import cn.bitterfree.api.common.util.convert.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 22:24
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MessageConverterConfig {

    @Bean
    public MessageConverter messageConverter(){
        // 1. 定义消息转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(JsonUtil.OBJECT_MAPPER);
        // 2. 配置自动创建消息 id，用于识别不同消息
        jackson2JsonMessageConverter.setCreateMessageIds(Boolean.TRUE);
        return jackson2JsonMessageConverter;
    }
}
