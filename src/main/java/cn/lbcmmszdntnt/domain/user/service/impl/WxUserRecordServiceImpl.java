package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.domain.user.model.dto.detail.LoginUser;
import cn.lbcmmszdntnt.domain.user.service.UserRecordService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.util.ExtractUtil;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 19:04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxUserRecordServiceImpl implements UserRecordService {

    private final RedisCache redisCache;

    private final UserService userService;

    @Override
    public Optional<LoginUser> getRecord(HttpServletRequest request) {
        if (ExtractUtil.isInTheTokenBlacklist(request)) {
            return Optional.empty();
        }
        String openid = ExtractUtil.getOpenIDFromJWT(request);
        String redisKey = JwtUtil.JWT_LOGIN_WX_USER + openid;
        return Optional.ofNullable(redisCache.getObject(redisKey, LoginUser.class)
                .orElseGet(() -> userService
                        .getUserByOpenid(openid)
                        .map(dbUser -> {
                            LoginUser loginUser = new LoginUser(dbUser, userService.getPermissions(dbUser.getId()));
                            redisCache.setObject(redisKey, loginUser, JwtUtil.JWT_TTL, JwtUtil.JWT_TTL_UNIT);
                            return loginUser;
                        }).orElse(null)
                ));
    }

    @Override
    public void deleteRecord(HttpServletRequest request) {
        String openid = ExtractUtil.getOpenIDFromJWT(request);
        redisCache.deleteObject(JwtUtil.JWT_LOGIN_WX_USER + openid);
    }
}