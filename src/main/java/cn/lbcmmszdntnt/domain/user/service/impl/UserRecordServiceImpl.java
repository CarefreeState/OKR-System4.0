package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.domain.user.model.dto.LoginUser;
import cn.lbcmmszdntnt.domain.user.service.UserRecordService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-12-24
 * Time: 10:56
 */
@Service
@RequiredArgsConstructor
public class UserRecordServiceImpl implements UserRecordService {

    private final RedisCache redisCache;

    private final UserService userService;

    @Override
    public Optional<LoginUser> getRecord(HttpServletRequest request, HttpServletResponse response) {
        if (UserRecordUtil.isInTheTokenBlacklist(request)) {
            return Optional.empty();
        }
        Long id = UserRecordUtil.getUserIdFromJWT(request, response);
        return userService.getUserById(id).map(user -> new LoginUser(user, userService.getPermissions(id)));
    }

}
