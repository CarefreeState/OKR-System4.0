package cn.bitterfree.api.domain.login.service.impl;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.auth.service.WxIdentifyService;
import cn.bitterfree.api.domain.login.model.dto.LoginDTO;
import cn.bitterfree.api.domain.login.model.dto.WxLoginDTO;
import cn.bitterfree.api.domain.login.service.LoginService;
import cn.bitterfree.api.domain.user.constants.UserConstants;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.domain.user.service.UserPhotoService;
import cn.bitterfree.api.domain.user.service.UserService;
import cn.bitterfree.api.redis.lock.RedisLock;
import cn.bitterfree.api.wxtoken.model.vo.JsCode2SessionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 12:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WxLoginServiceImpl implements LoginService {

    private final RedisLock redisLock;

    private final UserService userService;

    private final UserPhotoService userPhotoService;

    private final WxIdentifyService wxIdentifyService;

    @Override
    @Transactional
    public User login(LoginDTO loginDTO) {
        WxLoginDTO wxLoginDTO = Optional.ofNullable(loginDTO.getWxLoginDTO()).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.PARAM_IS_BLANK));
        // 1. 校验 code
        JsCode2SessionVO jsCode2Session = wxIdentifyService.validateCode(wxLoginDTO.getCode());
        // 2.  解析
        String openid = jsCode2Session.getOpenid();
        String unionid = jsCode2Session.getUnionid();
        // 如果用户不存在（微信用户未注册/绑定），则注册
        return redisLock.tryLockGetSomething(UserConstants.EXISTS_USER_WX_LOCK + openid, () -> {
            return userService.getUserByOpenid(openid).orElseGet(() -> {
                User user = new User();
                user.setOpenid(openid);
                user.setUnionid(unionid);
                user.setUsername(openid);
                user.setNickname(UserConstants.DEFAULT_WX_USER_NICKNAME);
                user.setPhoto(userPhotoService.getAnyOnePhoto());
                user.setUserType(UserConstants.DEFAULT_USER_TYPE);
                userService.registerUser(user);
                return user;
            });
        }, () -> null);
    }

}
