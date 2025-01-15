package cn.lbcmmszdntnt.domain.auth.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.auth.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.auth.model.dto.WxLoginDTO;
import cn.lbcmmszdntnt.domain.auth.service.LoginService;
import cn.lbcmmszdntnt.domain.user.constants.UserConstants;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.wxtoken.model.vo.JsCode2SessionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    private final static String DEFAULT_NICKNAME = "微信用户";

    private final UserService userService;

    @Override
    public User login(LoginDTO loginDTO) {
        WxLoginDTO wxLoginDTO = loginDTO.getWxLoginDTO();
        if(Objects.isNull(wxLoginDTO)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        // 1. 构造请求 + 发起请求 -> code2Session
        String code = wxLoginDTO.getCode();
        JsCode2SessionVO userFlag = userService.getUserFlag(code);
        // 2.  解析
        String openId = userFlag.getOpenid();
        if(Objects.isNull(openId)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_VALID);
        }
        // 3. 构造用户对象
        User user = new User();
        // 4. 尝试插入数据库
        user.setOpenid(openId);
        user.setUnionid(userFlag.getUnionid());
        userService.getUserByOpenid(openId)
                .ifPresentOrElse(dbUser -> {
                    user.setId(dbUser.getId());
                    // 更新一下数据 todo 没必要？
                    userService.lambdaUpdate().eq(User::getOpenid, openId).update(user);
                }, () -> {
                    user.setNickname(DEFAULT_NICKNAME);
                    user.setPhoto(UserConstants.getDefaultPhoto());
                    user.setUserType(UserConstants.DEFAULT_USER_TYPE);
                    userService.save(user);
                    log.info("新用户注册 -> {}", user);
                });
        return user;
    }

}
