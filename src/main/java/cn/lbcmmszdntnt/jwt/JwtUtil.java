package cn.lbcmmszdntnt.jwt;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * JWT 工具类
 */
@Slf4j
public class JwtUtil {

    // 设置秘钥明文
    private static final JwtProperties JWT_PROPERTIES = SpringUtil.getBean(JwtProperties.class);

    public static final String JWT_NAME = JWT_PROPERTIES.getTokenName();

    public static final String JWT_KEY = JWT_PROPERTIES.getSecretKey();

    private static final String APPLICATION_NAME = SpringUtil.getProperty("spring.application.name");

    public static final Long JWT_TTL = JWT_PROPERTIES.getTtl();

    public static final Long JWT_REFRESH_TIME = JWT_PROPERTIES.getRefreshTime();

    public static final TimeUnit JWT_UNIT = TimeUnit.DAYS;

    private static final String CUSTOM_CLAIMS_KEY = "custom";

    // 生成加密后的秘钥 secretKey
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    private static <T> JwtBuilder getJwtBuilder(String subject, T claims, Long ttlMillis, String uuid, TimeUnit timeUnit) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (Objects.isNull(ttlMillis) || Objects.isNull(timeUnit)) { // 只有其中一个也等于没有
            ttlMillis = JWT_UNIT.toMillis(JwtUtil.JWT_TTL);
        } else {
            ttlMillis = timeUnit.toMillis(ttlMillis);
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);

        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put(CUSTOM_CLAIMS_KEY, BeanUtil.beanToMap(claims));
        return Jwts.builder()
                // 设置自定义载荷
                .setClaims(customClaims)
                // 唯一的ID
                .setId(uuid)
                // 主题，可以是 JSON 数据
                .setSubject(subject)
                // 签发者
                .setIssuer(APPLICATION_NAME)
                // 签发时间
                .setIssuedAt(now)
                //使用 HS256 对称加密算法签名, 第二个参数为秘钥
                .signWith(signatureAlgorithm, secretKey)
                // 失效时间
                .setExpiration(expDate);
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static <T> String createJwt(String subject, T claims, Long ttlMillis, String id, TimeUnit timeUnit) {
        JwtBuilder builder = getJwtBuilder(subject, claims, ttlMillis, id, timeUnit);
        return builder.compact();
    }

    public static <T> String createJwt(String subject, T claims, Long ttlMillis, TimeUnit timeUnit) {
        return createJwt(subject, claims, ttlMillis, getUUID(), timeUnit);
    }

    public static <T> String createJwt(String subject, T claims) {
        return createJwt(subject, claims, null, null);
    }

    public static Claims parseJwt(String jwt) {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody(); // 获得载荷
    }

    public static long getJwtTTL(Claims claims) {
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    public static long getJwtTTL(String jwt) {
        return getJwtTTL(parseJwt(jwt));
    }

    public static <T> T getJwtKeyValue(String jwt, String key, Class<T> clazz) {
        return parseJwt(jwt).get(key, clazz);
    }

    public static Date getExpiredDate(String jwt) {
        Date result = null;
        try {
            result = parseJwt(jwt).getExpiration();
        } catch (ExpiredJwtException e) {
            result = e.getClaims().getExpiration();
        }
        return result;
    }

    public static boolean isTokenExpired(String jwt) {
        return getExpiredDate(jwt).before(new Date());
    }

    public static boolean validateToken(String jwt) {
        try {
            parseJwt(jwt);
        } catch (ExpiredJwtException e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static boolean judgeApproachExpiration(Claims claims) {
        return getJwtTTL(claims) < JWT_UNIT.toMillis(JWT_REFRESH_TIME);
    }

    public static boolean judgeApproachExpiration(String jwt) {
        return judgeApproachExpiration(parseJwt(jwt));
    }

    // 解析并无感刷新
    public static <T> T parseJwtData(String jwt, T data, HttpServletResponse response) {
        Claims claims = parseJwt(jwt);
        data = BeanUtil.fillBeanWithMap(claims.get(CUSTOM_CLAIMS_KEY, Map.class), data, Boolean.TRUE);
        String subject = claims.getSubject();
        if(Objects.nonNull(response) && judgeApproachExpiration(claims)) {
            response.setHeader(JWT_NAME, createJwt(subject, data));
        }
        return data;
    }

    public static String getJwtFromHeader(HttpServletRequest request) {
        return request.getHeader(JWT_NAME);
    }

    public static String getJwtFromParameter(HttpServletRequest request) {
        return request.getParameter(JWT_NAME);
    }

}