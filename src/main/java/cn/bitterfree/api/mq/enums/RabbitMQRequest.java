package cn.bitterfree.api.mq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 18:55
 */
@Getter
@AllArgsConstructor
public enum RabbitMQRequest {

    EXCHANGE_DETAILS("/exchanges/{virtualHost}/{exchange}?msg_rates_age=60&msg_rates_incr=5", "GET");

    private final String uri;

    private final String method;

}
