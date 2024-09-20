package cn.lbcmmszdntnt.util.jwt;

import cn.hutool.extra.spring.SpringUtil;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtil {

    public static final String JWT_HEADER = "Token";

    //设置秘钥明文
    private static final String JWT_KEY = SpringUtil.getProperty("key.jwt");

    private static final String applicationName = SpringUtil.getProperty("spring.application.name");

    public static final Long JWT_TTL = 1L; // 一天有效期

    public static final Long JWT_REFRESH_TIME = 3L; // 结束前三小时就无感刷新

    public static final TimeUnit JWT_TTL_UNIT = TimeUnit.DAYS;

    public static final TimeUnit JWT_REFRESH_TIME_UNIT = TimeUnit.HOURS;

    public static final String JWT_LOGIN_WX_USER = "jwtLoginWxUser:";

    public static final String JWT_LOGIN_EMAIL_USER = "jwtLoginEmailUser:";

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成jwt
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJwt(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID(), null);// 设置过期时间
        return builder.compact();
    }
 
    /**
     * 生成jwt
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJwt(String subject, Long ttlMillis, TimeUnit timeUnit) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID(), timeUnit);// 设置过期时间
        return builder.compact();
    }
 
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid, TimeUnit timeUnit) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (Objects.isNull(ttlMillis) || Objects.isNull(timeUnit)) { // 只有其中一个也等于没有
            ttlMillis = JWT_TTL_UNIT.toMillis(JwtUtil.JWT_TTL);
        } else {
            ttlMillis = timeUnit.toMillis(ttlMillis);
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
//                .setClaims() // 设置自定义载荷
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer(applicationName)     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate); // 失效时间
    }
 
    /**
     * 创建token
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJwt(String id, String subject, Long ttlMillis, TimeUnit timeUnit) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id, timeUnit);// 设置过期时间
        return builder.compact();
    }
 
    /**
     * 生成加密后的秘钥 secretKey
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }
    /**
     * 解析
     *
     * @param jwt
     * @return
     * @throws Exception
     */
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

    public static boolean judgeApproachExpiration(Claims claims) {
        return getJwtTTL(claims) < JWT_REFRESH_TIME_UNIT.toMillis(JWT_REFRESH_TIME);
    }

    public static boolean judgeApproachExpiration(String jwt) {
        return judgeApproachExpiration(parseJwt(jwt));
    }

    // 解析
    public static String parseJwtRawData(String jwt) {
        return parseJwt(jwt).getSubject();
    }

    // 解析并无感刷新
    public static String parseJwtRawData(String jwt, HttpServletResponse response) {
        Claims claims = parseJwt(jwt);
        String subject = claims.getSubject();
        if(Objects.nonNull(response) && Boolean.TRUE.equals(judgeApproachExpiration(claims))) {
            String newJwt = createJwt(subject);
            response.setHeader(JWT_HEADER, newJwt);
        }
        return subject;
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

    // 过期的话 parseJwt(jwt)会抛异常，也就是 “没抛异常能解析成功就是校验成功”,但是异常 ExpiredJwtException 的 getClaims 方法能获取到 payload
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

}