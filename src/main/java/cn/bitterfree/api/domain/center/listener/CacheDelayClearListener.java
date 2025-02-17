package cn.bitterfree.api.domain.center.listener;

import cn.bitterfree.api.domain.center.constants.CacheDelayClearConstants;
import cn.bitterfree.api.redis.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-20
 * Time: 19:07
 */
@Component
@RequiredArgsConstructor
public class CacheDelayClearListener {

    private final RedisCache redisCache;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = CacheDelayClearConstants.CLEAR_CACHE_QUEUE),
            exchange = @Exchange(name = CacheDelayClearConstants.CLEAR_CACHE_DELAY_DIRECT, delayed = "true"),
            key = CacheDelayClearConstants.CLEAR_CACHE
    ))
    public void cacheDelayClearListener(List<String> redisKeyList) {
        redisCache.deleteObjects(redisKeyList);
    }


}
