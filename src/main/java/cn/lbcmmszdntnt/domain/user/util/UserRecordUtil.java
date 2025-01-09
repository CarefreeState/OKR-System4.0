package cn.lbcmmszdntnt.domain.user.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.jwt.JwtUtil;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 19:58
 */
@Component
@Slf4j
public class UserRecordUtil {

    private static final RedisCache REDIS_CACHE = SpringUtil.getBean(RedisCache.class);

    private static final String TOKEN_BLACKLIST = "tokenBlacklist:";

    public static void joinTheTokenBlacklist() {
        Optional.ofNullable(InterceptorContext.getJwt()).filter(StringUtils::hasText).ifPresent(jwt -> {
            String redisKey = TOKEN_BLACKLIST + jwt;
            REDIS_CACHE.setObject(redisKey, Boolean.TRUE, JwtUtil.getJwtTTL(jwt), TimeUnit.MILLISECONDS);
        });
    }

    public static Boolean isInTheTokenBlacklist() {
        return Optional.ofNullable(InterceptorContext.getJwt()).filter(StringUtils::hasText).flatMap(jwt -> {
            return REDIS_CACHE.getObject(TOKEN_BLACKLIST + jwt, Boolean.class);
        }).orElse(Boolean.FALSE);
    }

    public static User getUserRecord() {
        return InterceptorContext.getUser();
    }

}
