package cn.bitterfree.api.domain.user.service.impl;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.user.constants.UserConstants;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.domain.user.service.UserService;
import cn.bitterfree.api.interceptor.service.UserInfoLoadService;
import cn.bitterfree.api.redis.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 12:22
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoLoadImpl implements UserInfoLoadService {

    private final UserService userService;

    private final RedisCache redisCache;

    @Override
    public User loadUser(Long userId) {
        // 暂不支持递归重定向（隔代的 id 映射不考虑，token 直接失效我也觉得合理）
        return userService.getUserById(userId).or(() -> redisCache
                        .getObject(UserConstants.USER_ID_REDIRECT + userId, Long.class)
                        .flatMap(userService::getUserById)
                ).orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.USER_ACCOUNT_NOT_EXIST));
    }
}
