package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.service.UserInfoLoadService;
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

    @Override
    public User loadUser(Long userId) {
        return userService.getUserById(userId).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.USER_ACCOUNT_NOT_EXIST));
    }
}
