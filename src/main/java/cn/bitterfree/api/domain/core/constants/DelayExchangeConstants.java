package cn.bitterfree.api.domain.core.constants;

/**
 * 值得注意的是，如果用 bean 的方式创建，这些配置应该与 RabbitMQ 的一致，否则会出错（甚至连累别的 exchange、queue、binding）
 */
public interface DelayExchangeConstants {

    String QUADRANT_DDL_DELAY_DIRECT = "quadrant.ddl.delay.direct";
    String FIRST_QUADRANT_DDL_QUEUE = "first.quadrant.ddl.queue";
    String SECOND_QUADRANT_DDL_QUEUE = "second.quadrant.ddl.queue";
    String THIRD_QUADRANT_DDL_QUEUE = "third.quadrant.ddl.queue";

    String FIRST_DDL = "first.ddl";
    String SECOND_DDL = "second.ddl";
    String THIRD_DDL = "third.ddl";
}