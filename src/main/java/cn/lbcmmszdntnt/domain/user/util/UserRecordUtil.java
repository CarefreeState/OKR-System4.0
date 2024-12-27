package cn.lbcmmszdntnt.domain.user.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.model.dto.LoginUser;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.model.vo.LoginTokenVO;
import cn.lbcmmszdntnt.domain.user.service.UserRecordService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.jwt.JwtUtil;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.security.config.SecurityConfig;
import cn.lbcmmszdntnt.util.thread.local.ThreadLocalMapUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

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

    private final static UserRecordService USER_RECORD_SERVICE = SpringUtil.getBean(UserRecordService.class);

    private static final RedisCache REDIS_CACHE = SpringUtil.getBean(RedisCache.class);

    private static final String TOKEN_BLACKLIST = "tokenBlacklist:";

    public static void joinTheTokenBlacklist(HttpServletRequest request) {
        String jwt = JwtUtil.getJwt(request);
        String redisKey = TOKEN_BLACKLIST + jwt;
        REDIS_CACHE.setObject(redisKey, Boolean.TRUE, JwtUtil.getJwtTTL(jwt), TimeUnit.MILLISECONDS);
    }

    public static Boolean isInTheTokenBlacklist(HttpServletRequest request) {
        String jwt = JwtUtil.getJwt(request);
        String redisKey = TOKEN_BLACKLIST + jwt;
        return REDIS_CACHE.getObject(redisKey, Boolean.class).orElse(Boolean.FALSE);
    }

    // 获取 json 中的数字类型的元素，要进行判断~
    public static Long getUserIdFromJWT(HttpServletRequest request, HttpServletResponse response) {
        LoginTokenVO loginTokenVO = JwtUtil.parseJwtFromHeader(request, response, new LoginTokenVO());
        return loginTokenVO.getUserId();
    }

    public static PreAuthenticatedAuthenticationToken getAuthenticationToken() {
        return ThreadLocalMapUtil.get(SecurityConfig.USER_SECURITY_RECORD, PreAuthenticatedAuthenticationToken.class);
    }

    public static PreAuthenticatedAuthenticationToken getAuthenticationToken(HttpServletRequest request, HttpServletResponse response) {
        // 若存在记录了就用之前的，避免重复进行业务
        return Optional.ofNullable(getAuthenticationToken()).orElseGet(() -> {
            LoginUser userRecord = USER_RECORD_SERVICE.getRecord(request, response).orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID));
            PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(userRecord, null, userRecord.getAuthorities());
            ThreadLocalMapUtil.set(SecurityConfig.USER_SECURITY_RECORD, authenticationToken);
            return authenticationToken;
        });
    }

    public static User getUserRecord() {
        LoginUser loginUser = (LoginUser) getAuthenticationToken().getPrincipal();
        return loginUser.getUser();
    }

}
