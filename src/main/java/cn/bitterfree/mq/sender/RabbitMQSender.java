package cn.bitterfree.mq.sender;

import cn.bitterfree.common.util.convert.UUIDUtil;
import cn.bitterfree.common.util.juc.threadpool.ThreadPoolUtil;
import cn.bitterfree.mq.config.PublisherReturnsCallBack;
import cn.bitterfree.mq.model.entity.RabbitMQMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 22:58
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RabbitMQSender {

    private final static ThreadPoolExecutor EXECUTOR = ThreadPoolUtil.getIoTargetThreadPool("Rabbit-MQ-Thread");

    private final RabbitTemplate rabbitTemplate;

    private final PublisherReturnsCallBack publisherReturnsCallBack;

    private final RabbitMessageConverter rabbitMessageConverter;

    @PostConstruct
    public void init() {
        rabbitTemplate.setTaskExecutor(EXECUTOR);
        // 设置统一的 publisher-returns（confirm 也可以设置统一的，但最好还是在发送时设置在 future 里）
        // rabbitTemplate 的 publisher-returns 同一时间只能存在一个
        // 因为 publisher confirm 后，其实 exchange 有没有转发成功，publisher 没必要每次发送都关注这个 exchange 的内部职责，更多的是“系统与 MQ 去约定”
        rabbitTemplate.setReturnsCallback(publisherReturnsCallBack);
    }

    private final static Function<Throwable, ? extends CorrelationData.Confirm> ON_FAILURE = ex -> {
        log.error("处理 ack 回执失败, {}", ex.getMessage());
        return null;
    };

    private MessagePostProcessor delayMessagePostProcessor(long delay) {
        // 值得注意的是，delay 如果超过 int 的范围，会导致 delay 小于 0 等非目标效果的情况！（delay 最大只能是大概 49 天）
        return message -> {
            // 小于 0 也是立即执行
            // setDelay 才是给 RabbitMQ 看的，setReceivedDelay 是给 publish-returns 看的
            message.getMessageProperties().setDelay((int) Math.max(delay, 0));
            return message;
        };
    };

    private CorrelationData newCorrelationData() {
        return new CorrelationData(UUIDUtil.uuid32());
    }

    private <T> void send(RabbitMQMessage<T> rabbitMQMessage) {
        String exchange = rabbitMQMessage.getExchange();
        String routingKey = rabbitMQMessage.getRoutingKey();
        T msg = rabbitMQMessage.getMsg();
        long delay = rabbitMQMessage.getDelay();
        int maxRetries = rabbitMQMessage.getMaxRetries();
        log.info("准备发送消息，exchange: {}, routingKey: {}, msg: {}, delay: {}s, maxRetries: {}",
                exchange, routingKey, msg, TimeUnit.MILLISECONDS.toSeconds(delay), maxRetries);

        CorrelationData correlationData = newCorrelationData();
        MessagePostProcessor delayMessagePostProcessor = delayMessagePostProcessor(delay);
        correlationData.getFuture().exceptionallyAsync(ON_FAILURE, EXECUTOR).thenAcceptAsync(new Consumer<>() {

            private int retryCount = 0; // 一次 send 从始至终都用的是一个 Consumer 对象，所以作用的都是同一个计数器

            @Override
            public void accept(CorrelationData.Confirm confirm) {
                Optional.ofNullable(confirm).ifPresent(c -> {
                    if(c.isAck()) {
                        log.info("ACK {} 消息成功到达，{}", correlationData.getId(), c.getReason());
                    } else {
                        log.warn("NACK {} 消息未能到达，{}", correlationData.getId(), c.getReason());
                        if(retryCount >= maxRetries) {
                            log.error("次数到达上限 {}", maxRetries);
                            return;
                        }
                        retryCount++;
                        log.warn("开始第 {} 次重试", retryCount);
                        CorrelationData cd = newCorrelationData();
                        cd.getFuture().exceptionallyAsync(ON_FAILURE, EXECUTOR).thenAcceptAsync(this, EXECUTOR);
                        rabbitTemplate.convertAndSend(exchange, routingKey, msg, delayMessagePostProcessor, cd);
                    }
                });
            }
        }, EXECUTOR);
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, delayMessagePostProcessor, correlationData);
    }

    /**
     * @param exchange 交换机
     * @param routingKey routing key
     * @param msg 消息
     * @param delay 延迟时间（如果是延迟交换机，delay 才有效）
     * @param maxRetries 最大重试机会
     * @param <T> 消息的对象类型
     */
    public <T> void send(String exchange, String routingKey, T msg, long delay, int maxRetries){
        send(rabbitMessageConverter.getRabbitMQMessage(exchange, routingKey, msg, delay, maxRetries));
    }

    public void sendMessage(String exchange, String routingKey, Object msg) {
        send(exchange, routingKey, msg, 0, 0);
    }

    public void sendDelayMessage(String exchange, String routingKey, Object msg, long delay){
        send(exchange, routingKey, msg, delay, 0);
    }

    public void sendWithConfirm(String exchange, String routingKey, Object msg, int maxReties) {
        send(exchange, routingKey, msg, 0, maxReties);
    }

    public void sendDelayMessageWithConfirm(String exchange, String routingKey, Object msg, long delay, int maxReties) {
        send(exchange, routingKey, msg, delay, maxReties);
    }

}
