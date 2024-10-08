package cn.lbcmmszdntnt.domain.user.service;

import cn.lbcmmszdntnt.domain.user.model.dto.UserinfoDTO;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-01-22 14:18:10
*/
public interface UserService extends IService<User> {

    String getUserFlag(String code);

    List<String> getPermissions(Long userId);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByOpenid(String openid);

    void deleteUserIdCache(Long id);

    void deleteUserEmailCache(String email);

    void deleteUserOpenidCache(String openid);

    void deleteUserAllCache(Long id);

    void improveUserinfo(UserinfoDTO userinfoDTO, Long userId);

    void bindingEmail(Long userId, String email, String code, String recordEmail);

    void bindingWx(Long userId, String randomCode, String code);

    String tryUploadPhoto(byte[] photoData, Long userId, String originPhoto);

    void onLoginState(String secret, String openId, String unionid);

    Map<String, Object> checkLoginState(String secret);

}