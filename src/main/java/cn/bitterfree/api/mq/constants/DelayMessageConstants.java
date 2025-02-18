package cn.bitterfree.api.mq.constants;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-12
 * Time: 22:56
 */
public interface DelayMessageConstants {

    long GLOBAL_12_DAYS_TTL = TimeUnit.DAYS.toMillis(12);
    long GLOBAL_24_DAYS_TTL = TimeUnit.DAYS.toMillis(24);
    String GLOBAL_12_DAYS_TTL_QUEUE = "global.12days.ttl.queue";
    String GLOBAL_24_DAYS_TTL_QUEUE = "global.24days.ttl.queue";
    Map<Long, String> GLOBAL_DELAY_TTL_MAP = Map.of(
            GLOBAL_12_DAYS_TTL, GLOBAL_12_DAYS_TTL_QUEUE,
            GLOBAL_24_DAYS_TTL, GLOBAL_24_DAYS_TTL_QUEUE
    );

    String GLOBAL_DELAY_DIRECT = "";
    String GLOBAL_DELAY_QUEUE = "global.delay.queue";
//    String DELAY_EXCHANGE_MESSAGE_COUNTER = "delayExchangeMessageCounter:";
//    Long DELAY_EXCHANGE_MESSAGE_COUNTER_TTL = 12L; // 长达十二天未变化，其内部的消息早没了
//    TimeUnit DELAY_EXCHANGE_MESSAGE_COUNTER_UNIT = TimeUnit.DAYS;
    int MAX_DELAY_EXCHANGE_CAPACITY = 10_0000; // 10w 条内比较安全

    String DELAY_EXCHANGE_MESSAGE_CACHE_LIST = "delayExchangeMessageCacheList:";
    Long DELAY_EXCHANGE_MESSAGE_CACHE_LIST_TTL = 12L; // 同理若长达十二天未变化，其内部的消息早没了
    TimeUnit DELAY_EXCHANGE_MESSAGE_CACHE_LIST_UNIT = TimeUnit.DAYS;
    Long DELAY_EXCHANGE_MESSAGE_CACHE_LISTEN_GAP = TimeUnit.MINUTES.toMillis(5);

    String LOCAL_DELAY_QUEUE = "local.delay.queue";

}
