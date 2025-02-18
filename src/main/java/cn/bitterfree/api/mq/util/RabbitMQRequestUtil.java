package cn.bitterfree.api.mq.util;

import cn.bitterfree.api.common.util.convert.EncryptUtil;
import cn.bitterfree.api.common.util.web.HttpRequestUtil;
import cn.bitterfree.api.mq.config.RabbitMQConfig;
import cn.bitterfree.api.mq.enums.RabbitMQRequest;
import cn.bitterfree.api.mq.model.vo.DelayExchangeVO;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.util.Map;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-18
 * Time: 15:19
 */
@Slf4j
public class RabbitMQRequestUtil {

    private final static RabbitMQConfig RABBIT_MQ_CONFIG = SpringUtil.getBean(RabbitMQConfig.class);
    private final static Map<String, String> AUTHORIZATION_HEADER = Map.of(
            HttpHeaders.AUTHORIZATION,
            "Basic " + EncryptUtil.encodeBase64(String.format("%s:%s", RABBIT_MQ_CONFIG.getUsername(), RABBIT_MQ_CONFIG.getPassword()))
    );

    private final static String EXCHANGE_DETAILS_BASE_URL = HttpRequestUtil.getBaseUrl(RABBIT_MQ_CONFIG.getHttpApi(), RabbitMQRequest.EXCHANGE_DETAILS.getUri());

    public static int getMessagesDelayed(String exchange) {
        String httpUrl = HttpRequestUtil.buildUrl(
                EXCHANGE_DETAILS_BASE_URL,
                null,
                RABBIT_MQ_CONFIG.getVirtualHost(), exchange
        );
        DelayExchangeVO delayExchangeVO = HttpRequestUtil.jsonRequest(
                httpUrl,
                RabbitMQRequest.EXCHANGE_DETAILS.getMethod(),
                null,
                DelayExchangeVO.class,
                AUTHORIZATION_HEADER
        );
        int count = Optional.ofNullable(delayExchangeVO).map(DelayExchangeVO::getMessagesDelayed).orElse(0);
        log.info("查询延时交换机 {} 消息数 {}", exchange, count);
        return count;
    }

}
