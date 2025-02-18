package cn.bitterfree.api.mq.client;

import cn.bitterfree.api.common.util.convert.EncryptUtil;
import cn.bitterfree.api.common.util.web.HttpRequestUtil;
import cn.bitterfree.api.mq.config.RabbitMQConfig;
import cn.bitterfree.api.mq.enums.RabbitMQRequest;
import cn.bitterfree.api.mq.model.vo.DelayExchangeVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-19
 * Time: 0:40
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQHttpClient {

    private final RabbitMQConfig rabbitMQConfig;

    private Map<String, String> authorizationHeader;

    @PostConstruct
    public void init() {
        Map.of(
                HttpHeaders.AUTHORIZATION,
                "Basic " + EncryptUtil.encodeBase64(String.format("%s:%s", rabbitMQConfig.getUsername(), rabbitMQConfig.getPassword()))
        );
    }

    public int getMessagesDelayed(String exchange) {
        String httpUrl = HttpRequestUtil.buildUrl(
                HttpRequestUtil.getBaseUrl(rabbitMQConfig.getHttpApi(), RabbitMQRequest.EXCHANGE_DETAILS.getUri()),
                null,
                rabbitMQConfig.getVirtualHost(), exchange
        );
        DelayExchangeVO delayExchangeVO = HttpRequestUtil.jsonRequest(
                httpUrl,
                RabbitMQRequest.EXCHANGE_DETAILS.getMethod(),
                null,
                DelayExchangeVO.class,
                authorizationHeader
        );
        int count = Optional.ofNullable(delayExchangeVO).map(DelayExchangeVO::getMessagesDelayed).orElse(0);
        log.info("查询延时交换机 {} 消息数 {}", exchange, count);
        return count;
    }

}
