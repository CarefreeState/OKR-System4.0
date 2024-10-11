package cn.lbcmmszdntnt.domain.user.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import cn.lbcmmszdntnt.util.jwt.JwtUtil;
import cn.lbcmmszdntnt.util.web.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 13:51
 */
@Slf4j
public class ExtractUtil {

    public final static String OPENID = "openid";

    public final static String UNIONID = "unionid";

    public final static String SESSION_KEY = "session_key";

    public final static String ID = "id";

    private static final RedisCache REDIS_CACHE = SpringUtil.getBean(RedisCache.class);

    private static final String TOKEN_BLACKLIST = "tokenBlacklist:";


    public static String getJWTRawDataOnRequest(HttpServletRequest request) {
        // 获取本次请求的响应对象
        HttpServletResponse response = HttpUtil.getResponse();
        String token = request.getHeader(JwtUtil.JWT_HEADER);
        return Optional.ofNullable(token).map(jwt -> JwtUtil.parseJwtRawData(jwt, response)).orElse(null);
    }

    public static void joinTheTokenBlacklist(HttpServletRequest request) {
        String token = request.getHeader(JwtUtil.JWT_HEADER);
        String redisKey = TOKEN_BLACKLIST + token;
        long ttl = JwtUtil.getJwtTTL(token);
        REDIS_CACHE.setCacheObject(redisKey, Boolean.TRUE, ttl, TimeUnit.MILLISECONDS);
    }

        public static Boolean isInTheTokenBlacklist(HttpServletRequest request) {
        String token = request.getHeader(JwtUtil.JWT_HEADER);
        String redisKey = TOKEN_BLACKLIST + token;
        return (Boolean) REDIS_CACHE.getCacheObject(redisKey).orElse(Boolean.FALSE);
    }

    public static <T> T getValueFromJWT(HttpServletRequest request, String key, Class<T> clazz) {
        String rawData = getJWTRawDataOnRequest(request);
        return JsonUtil.analyzeJsonField(rawData, key, clazz);
    }

    public static String getOpenIDFromJWT(HttpServletRequest request) {
        return getValueFromJWT(request, OPENID, String.class);
    }

    public static String getUnionIDFromJWT(HttpServletRequest request) {
        return getValueFromJWT(request, UNIONID, String.class);
    }

    public static String getSessionKeyFromJWT(HttpServletRequest request) {
        return getValueFromJWT(request, SESSION_KEY, String.class);
    }

    // 获取 json 中的数字类型的元素，要进行判断~
    public static Long getUserIdFromJWT(HttpServletRequest request) {
        return getValueFromJWT(request, ID, Long.class);
    }

}
