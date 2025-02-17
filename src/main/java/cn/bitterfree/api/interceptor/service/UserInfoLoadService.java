package cn.bitterfree.api.interceptor.service;

import cn.bitterfree.api.domain.user.model.entity.User;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 21:08
 */
public interface UserInfoLoadService {

    User loadUser(Long userId);

}
