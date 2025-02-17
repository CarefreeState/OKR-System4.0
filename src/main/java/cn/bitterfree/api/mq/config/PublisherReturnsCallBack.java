package cn.bitterfree.api.mq.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 23:37
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublisherReturnsCallBack implements RabbitTemplate.ReturnsCallback {

    // 不存在 routing key 对应的队列，那在我看来转发到零个是合理的现象，但在这里也认为是路由失败（MQ 认为消息一定至少要进入一个队列，之后才能被处理，这就是可靠性）（反正就是回执了，你爱咋处理是你自己的事情）
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        // 可能一些版本的 mq 会因为是延时交换机，导致发送者回执，只要没有 NACK 这种情况其实并不是不可靠（其实我也不知道有没有版本会忽略）
        // 但是其实不忽略也不错，毕竟者本来就是特殊情况，一般交换机是不存储的，但是这个临时存储消息
        // 但这样也就代表了，延时后消息路由失败是没法再次处理的（因为我们交给延时交换机后就不管了，可靠性有 mq 自己保持）
        MessageProperties messageProperties = returnedMessage.getMessage().getMessageProperties();
        // 这里的 message 并不是原本的 message，是额外的封装，x-delay 在 publish-returns 里面封装到 receiveDelay 里了
        Integer delay = messageProperties.getReceivedDelay();
        // 如果不是延时交换机，却设置了 delay 大于 0，是不会延时的，所以是其他原因导致的（以防万一把消息记录到日志里）
        if(Objects.nonNull(delay) && delay.compareTo(0) > 0) {
            log.info("交换机 {}, 路由键 {} 消息 {} 延迟 {} s", returnedMessage.getExchange(), returnedMessage.getRoutingKey(), messageProperties, TimeUnit.MILLISECONDS.toSeconds(delay));
            return;
        }
        log.warn("publisher-returns 发送者回执(应答码{}，应答内容{})(消息 {} 成功到达交换机 {}，但路由失败，路由键为 {})",
                returnedMessage.getReplyCode(), returnedMessage.getReplyText(), returnedMessage.getMessage(),
                returnedMessage.getExchange(), returnedMessage.getRoutingKey());
    }
}
