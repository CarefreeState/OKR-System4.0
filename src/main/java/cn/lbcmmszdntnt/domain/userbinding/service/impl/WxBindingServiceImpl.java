package cn.lbcmmszdntnt.domain.userbinding.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.auth.service.BindingAckIdentifyService;
import cn.lbcmmszdntnt.domain.user.constants.UserConstants;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.userbinding.model.dto.BindingDTO;
import cn.lbcmmszdntnt.domain.userbinding.model.dto.WxBindingDTO;
import cn.lbcmmszdntnt.domain.userbinding.service.BindingService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import cn.lbcmmszdntnt.wxtoken.model.vo.JsCode2SessionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 22:24
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WxBindingServiceImpl implements BindingService {

    private final RedisLock redisLock;

    private final UserService userService;

    private final BindingAckIdentifyService bindingAckIdentifyService;

    @Override
    public void binding(User user, BindingDTO bindingDTO) {
        WxBindingDTO wxBindingDTO = Optional.ofNullable(bindingDTO.getWxBindingDTO()).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.PARAM_IS_BLANK));
        // 判断当前用户是否绑定了微信
        if(StringUtils.hasText(user.getOpenid())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_BOUND_WX);
        }
        // 1. 校验 code
        JsCode2SessionVO jsCode2Session = bindingAckIdentifyService.validateSecret(wxBindingDTO.getSecret());
        // 2.  解析
        String openid = jsCode2Session.getOpenid();
        String unionid = jsCode2Session.getUnionid();
        // 若没人使用这个 openid 就绑定
        redisLock.tryLockDoSomething(UserConstants.EXISTS_USER_WX_LOCK + openid, () -> {
            userService.getUserByOpenid(openid).ifPresentOrElse(wxUser -> {
                throw new GlobalServiceException(GlobalServiceStatusCode.WX_USER_BE_BOUND);
            }, () -> {
                Long userId = user.getId();
                // 绑定
                log.info("用户 {} 成功绑定微信 {}", userId, openid);
                userService.lambdaUpdate().eq(User::getId, userId).set(User::getOpenid, openid).update();
            });
        }, () -> {});
    }

}
