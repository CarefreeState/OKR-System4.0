package cn.bitterfree.api.mq.client;

import cn.bitterfree.api.common.util.convert.UUIDUtil;
import cn.bitterfree.api.mq.config.PublisherReturnsCallBack;
import cn.bitterfree.api.mq.constants.DelayMessageConstants;
import cn.bitterfree.api.mq.model.entity.RabbitMQMessage;
import cn.bitterfree.api.redis.cache.RedisZSetCache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
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

    private final RedisZSetCache redisZSetCache;

    private final RabbitTemplate rabbitTemplate;

    private final PublisherReturnsCallBack publisherReturnsCallBack;

    private final RabbitMessageConverter rabbitMessageConverter;

    private final RabbitMQHttpClient rabbitMQHttpClient;

    public interface RabbitMessageConverter {

        <T> RabbitMQMessage<?> getRabbitMQMessage(String exchange, String routingKey, T msg, long delay, int maxRetries);

    }

    @PostConstruct
    public void init() {
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
            if(delay > 0) {
                message.getMessageProperties().setDelay((int) delay);
            }
            return message;
        };
    };

    private CorrelationData newCorrelationData() {
        return new CorrelationData(UUIDUtil.uuid32());
    }

    private <T> void localDelaySend(RabbitMQMessage<T> rabbitMQMessage) {
        // 要避免都在集中在一个服务器，这个本地延时任务应该分发出去
        log.info("delay 为 {}s 的消息分发 {} ，由服务器实现延时", TimeUnit.MILLISECONDS.toSeconds(rabbitMQMessage.getDelay()), DelayMessageConstants.LOCAL_DELAY_QUEUE);
        sendMessage("", DelayMessageConstants.LOCAL_DELAY_QUEUE, rabbitMQMessage);
    }

    private <T> boolean trySend(RabbitMQMessage<T> rabbitMQMessage) {
        long delay = rabbitMQMessage.getDelay();
        String exchange = rabbitMQMessage.getExchange();
        if (delay > 0 && rabbitMQHttpClient.getMessagesDelayed(exchange) >= DelayMessageConstants.MAX_DELAY_EXCHANGE_CAPACITY) {
            log.info("延时交换机 {} 已达到安全值范围 {}，后续的延时消息将交给服务器实现延时或先进行缓存", exchange, DelayMessageConstants.MAX_DELAY_EXCHANGE_CAPACITY);
            if (delay < DelayMessageConstants.DELAY_EXCHANGE_MESSAGE_CACHE_LISTEN_GAP) {
                localDelaySend(rabbitMQMessage);
            } else {
                String messageCacheList = DelayMessageConstants.DELAY_EXCHANGE_MESSAGE_CACHE_LIST + exchange;
                redisZSetCache.add(messageCacheList, rabbitMQMessage, delay + System.currentTimeMillis(),
                        DelayMessageConstants.DELAY_EXCHANGE_MESSAGE_CACHE_LIST_TTL, DelayMessageConstants.DELAY_EXCHANGE_MESSAGE_CACHE_LIST_UNIT);
            }
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private <T> void send(RabbitMQMessage<T> rabbitMQMessage) {
        if(Boolean.FALSE.equals(trySend(rabbitMQMessage))) {
            return;
        }
        String exchange = rabbitMQMessage.getExchange();
        long delay = rabbitMQMessage.getDelay(); // 这里的 delay 是准确的，才能到这里
        String routingKey = rabbitMQMessage.getRoutingKey();
        T msg = rabbitMQMessage.getMsg();
        int maxRetries = rabbitMQMessage.getMaxRetries();
        log.info("准备发送消息，exchange: {}, routingKey: {}, msg: {}, delay: {}s, maxRetries: {}",
                exchange, routingKey, msg, TimeUnit.MILLISECONDS.toSeconds(delay), maxRetries);
        CorrelationData correlationData = newCorrelationData();
        MessagePostProcessor delayMessagePostProcessor = delayMessagePostProcessor(delay);
        correlationData.getFuture().exceptionallyAsync(ON_FAILURE).thenAcceptAsync(new Consumer<>() {

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
                        cd.getFuture().exceptionallyAsync(ON_FAILURE).thenAcceptAsync(this);
                        rabbitTemplate.convertAndSend(exchange, routingKey, msg, delayMessagePostProcessor, cd);
                    }
                });
            }
        });
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
    private <T> void send(String exchange, String routingKey, T msg, long delay, int maxRetries){
        send(rabbitMessageConverter.getRabbitMQMessage(exchange, routingKey, msg, delay, maxRetries));
    }

    public void sendMessage(String exchange, String routingKey, Object msg) {
        send(exchange, routingKey, msg, 0, 0);
    }

    public void sendWithConfirm(String exchange, String routingKey, Object msg, int maxReties) {
        send(exchange, routingKey, msg, 0, maxReties);
    }

    public void sendDelayMessage(String exchange, String routingKey, Object msg, long delay){
        send(exchange, routingKey, msg, delay, 0);
    }

    public void sendDelayMessageWithConfirm(String exchange, String routingKey, Object msg, long delay, int maxReties) {
        send(exchange, routingKey, msg, delay, maxReties);
    }

    // 只有从 ZSet 里查出来的 message 的 delay 不准确，需要更新，其他情况 delay 都认为是准确的
    public void popAndLocalDelaySend(String exchange) {
        String messageCacheListKey = DelayMessageConstants.DELAY_EXCHANGE_MESSAGE_CACHE_LIST + exchange;
        // ddl 在未来五分钟内的延时任务直接用用任务调度线程池做延时功能
        long min = 0;
        long max = System.currentTimeMillis() + DelayMessageConstants.DELAY_EXCHANGE_MESSAGE_CACHE_LISTEN_GAP;
        redisZSetCache.popRangeByScore(messageCacheListKey, RabbitMQMessage.class, min, max).forEach(tuple -> {
            RabbitMQMessage<?> message = tuple.getValue();
            message.setDelay(tuple.getScore().longValue() - System.currentTimeMillis()); // 计算准确的 delay
            this.localDelaySend(message);
        });
    }

    public void popAndSendDelayMessage(String exchange) {
        String messageCacheListKey = DelayMessageConstants.DELAY_EXCHANGE_MESSAGE_CACHE_LIST + exchange;
        // 计算还剩多少个缺口
        int opening = DelayMessageConstants.MAX_DELAY_EXCHANGE_CAPACITY - rabbitMQHttpClient.getMessagesDelayed(exchange);
        // 若还有缺口就重新发送消息
        if (opening > 0) {
            // 这里的消息都是要直接加入延时交换机的
            redisZSetCache.popMin(messageCacheListKey, RabbitMQMessage.class, opening).forEach(tuple -> {
                RabbitMQMessage<?> message = tuple.getValue();
                message.setDelay(tuple.getScore().longValue() - System.currentTimeMillis()); // 计算准确的 delay
                this.send(message);
            });
        }
        popAndLocalDelaySend(exchange);
    }

}
