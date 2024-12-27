package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.constants.UserPhotoConstants;
import cn.lbcmmszdntnt.domain.user.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.WxLoginDTO;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.model.vo.WxCode2SessionVO;
import cn.lbcmmszdntnt.domain.user.service.LoginService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
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
        String resultJson = userService.getUserFlag(code);
        // 2.  解析
        WxCode2SessionVO wxCode2SessionVO = JsonUtil.parse(resultJson, WxCode2SessionVO.class);
        String openId = wxCode2SessionVO.getOpenId();
        if(Objects.isNull(openId)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_VALID);
        }
        // 3. 构造用户对象
        User user = new User();
        // 4. 尝试插入数据库
        // todo: 多个 openid 用 unionid 去判断是否是同一个用户（需要的时候再去写）
        user.setOpenid(openId);
        user.setUnionid(wxCode2SessionVO.getUnionId());
        userService.getUserByOpenid(openId)
                .ifPresentOrElse(dbUser -> {
                    user.setId(dbUser.getId());
                    // 更新一下数据
                    userService.lambdaUpdate().eq(User::getOpenid, openId).update(user);
                }, () -> {
                    user.setNickname(DEFAULT_NICKNAME);
                    user.setPhoto(UserPhotoConstants.getDefaultPhoto());
                    userService.save(user);
                    log.info("新用户注册 -> {}", user);
                });
        return user;
    }

    @Override
    public void logout(HttpServletRequest request) {
        UserRecordUtil.joinTheTokenBlacklist(request);
    }
}
