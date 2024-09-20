package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.domain.user.model.dto.detail.LoginUser;
import cn.lbcmmszdntnt.domain.user.service.UserRecordService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.util.ExtractUtil;
import cn.lbcmmszdntnt.redis.RedisCache;
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
public class EmailUserRecordServiceImpl implements UserRecordService {

    private final RedisCache redisCache;

    private final UserService userService;

    @Override
    public Optional<LoginUser> getRecord(HttpServletRequest request) {
        if (ExtractUtil.isInTheTokenBlacklist(request)) {
            return Optional.empty();
        }
        Long id = ExtractUtil.getUserIdFromJWT(request);
        String redisKey = JwtUtil.JWT_LOGIN_EMAIL_USER + id;
        return Optional.ofNullable((LoginUser) redisCache.getCacheObject(redisKey)
                .orElseGet(() -> userService
                        .getUserById(id)
                        .map(dbUser -> {
                            LoginUser loginUser = new LoginUser(dbUser, userService.getPermissions(id));
                            redisCache.setCacheObject(redisKey, loginUser, JwtUtil.JWT_TTL, JwtUtil.JWT_TTL_UNIT);
                            return loginUser;
                        }).orElse(null)
                ));
    }

    @Override
    public void deleteRecord(HttpServletRequest request) {
        Long id = ExtractUtil.getUserIdFromJWT(request);
        redisCache.deleteObject(JwtUtil.JWT_LOGIN_EMAIL_USER + id);
    }
}
