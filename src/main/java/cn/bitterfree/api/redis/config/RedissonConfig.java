package cn.bitterfree.api.redis.config;

import lombok.Getter;
import lombok.Setter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-03
 * Time: 10:55
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.redisson")
public class RedissonConfig {

    private String url;

    private String password;

    private Integer database;

    private RedisLockProperties lock;

    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        // 配置 Redisson 连接信息
        config.useSingleServer()
                .setAddress(url)
                .setPassword(Optional.ofNullable(password).filter(StringUtils::hasText).orElse(null)) // 设置密码
                .setDatabase(database) // 设置数据库索引
        ;
        return Redisson.create(config);
    }

    @Bean
    public RedisLockProperties redisLockProperties() {
        return this.lock;
    }

}
