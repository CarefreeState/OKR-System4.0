package cn.bitterfree.api.domain.user.service;

import cn.bitterfree.api.domain.user.enums.UserType;
import cn.bitterfree.api.domain.user.model.dto.UserQueryDTO;
import cn.bitterfree.api.domain.user.model.dto.UserinfoDTO;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.domain.user.model.vo.UserQueryVO;
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

    UserQueryVO queryUser(UserQueryDTO userQueryDTO);
    void updateUserType(Long userId, UserType userType);

}