package cn.bitterfree.api.mq.client;

import cn.bitterfree.api.mq.model.vo.DelayExchangeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-03-05
 * Time: 16:27
 */
@FeignClient(name = "rabbitmq-service", url = "${okr.mq.http-api}")
public interface RabbitMQHttpFeignClient {

    @GetMapping("/exchanges/{virtualHost}/{exchange}?msg_rates_age=60&msg_rates_incr=5")
    DelayExchangeVO getMessagesDelayed(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                       @PathVariable("virtualHost") String virtualHost,
                                       @PathVariable("exchange") String exchange);

}
