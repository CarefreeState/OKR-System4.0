package cn.bitterfree.api.redis.config;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-13
 * Time: 0:34
 */
@Data
public class RedisLockProperties {

    private Long wait;

    private Long timeout;

    private TimeUnit unit;

}
