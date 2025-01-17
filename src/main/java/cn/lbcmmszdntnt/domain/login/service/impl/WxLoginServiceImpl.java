package cn.lbcmmszdntnt.domain.login.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.auth.service.WxIdentifyService;
import cn.lbcmmszdntnt.domain.login.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.login.model.dto.WxLoginDTO;
import cn.lbcmmszdntnt.domain.login.service.LoginService;
import cn.lbcmmszdntnt.domain.user.constants.UserConstants;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserPhotoService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import cn.lbcmmszdntnt.wxtoken.model.vo.JsCode2SessionVO;
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
