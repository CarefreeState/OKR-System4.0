package cn.lbcmmszdntnt.domain.user.service;

import cn.lbcmmszdntnt.domain.user.model.dto.UserinfoDTO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-01-22 14:18:10
*/
public interface UserService extends IService<User> {

    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByOpenid(String openid);

    User checkAndGetUserByUsername(String username);
    User registerUser(User user);

    void clearUserAllCache(Long id);

    void improveUserinfo(UserinfoDTO userinfoDTO, Long userId);

}