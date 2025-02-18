package cn.bitterfree.api.mq.config;

import cn.bitterfree.api.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.api.domain.core.constants.DelayExchangeConstants;
import cn.bitterfree.api.mq.constants.DelayMessageConstants;
import cn.bitterfree.api.mq.sender.RabbitMQSender;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-18
 * Time: 1:42
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DelayExchangeMessageCacheXxlJobConfig {

    private final static String ROUTE = "ROUND";
    private final static int TRIGGER_STATUS = 1; // 不启动
    private final static String CRON = "0 0/5 * * * ? *"; // 每五分钟

    private final RabbitMQSender rabbitMQSender;

    private final RedisCache redisCache;

    // 热点时间的任务最多延时五秒
    @XxlJob(value = "listenDelayExchangeMessageCache")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE, triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】每五分钟一次的延时交换机消息数扫描")
    public void listenDelayExchangeMessageCache() {
        // 常数级的，所以不需要多线程分批处理
        redisCache.getKeysByPrefix(DelayMessageConstants.DELAY_EXCHANGE_MESSAGE_CACHE_LIST).forEach(counterKey -> {
            // 获取 exchange
            String exchange = counterKey.replaceFirst(DelayMessageConstants.DELAY_EXCHANGE_MESSAGE_CACHE_LIST, "");
            rabbitMQSender.popAndSendDelayMessage(exchange);
        });

    }
}
