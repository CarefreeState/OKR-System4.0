package cn.lbcmmszdntnt.domain.center.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.center.constants.CacheDelayClearConstants;
import cn.lbcmmszdntnt.mq.sender.RabbitMQSender;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-20
 * Time: 19:33
 */
public class CacheDelayClearUtil {

    private final static RabbitMQSender RABBIT_MQ_SENDER = SpringUtil.getBean(RabbitMQSender.class);

    public static void delayClear(List<String> redisKeyList) {
        if(!CollectionUtils.isEmpty(redisKeyList)) {
            RABBIT_MQ_SENDER.sendDelayMessage(
                    CacheDelayClearConstants.CLEAR_CACHE_DELAY_DIRECT,
                    CacheDelayClearConstants.CLEAR_CACHE,
                    redisKeyList,
                    CacheDelayClearConstants.CLEAR_CACHE_DELAY
            );
        }
    }

}
