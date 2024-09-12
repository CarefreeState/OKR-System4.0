package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.model.dto.WxLoginDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.unify.LoginDTO;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.domain.user.service.LoginService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.domain.user.util.ExtractUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import cn.lbcmmszdntnt.util.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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

    private final static String DEFAULT_PHOTO = "media/static/default.png";

    private final UserService userService;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        WxLoginDTO wxLoginDTO = loginDTO.getWxLoginDTO();
        if(Objects.isNull(wxLoginDTO)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        // 1. 构造请求 + 发起请求 -> code2Session
        String code = wxLoginDTO.getCode();
        String resultJson = userService.getUserFlag(code);
        // 2.  解析
        Map<String, Object> response = JsonUtil.analyzeJson(resultJson, Map.class);
        String openid = (String) response.get("openid");
        String unionid = (String) response.get("unionid");
        String sessionKey = (String) response.get("session_key");
        if(Objects.isNull(openid)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.WX_CODE_NOT_VALID);
        }
        // 3. 构造用户对象
        User user = wxLoginDTO.transToUser();
        user.setOpenid(openid);
        user.setUnionid(unionid);
        // 4. 尝试插入数据库
        // todo: 多个 openid 用 unionid 去判断是否是同一个用户（需要的时候再去写）
        User dbUser = userService.lambdaQuery().eq(User::getOpenid, openid).one();
        if(Objects.isNull(dbUser)) {
            user.setNickname(DEFAULT_NICKNAME);
            user.setPhoto(DEFAULT_PHOTO);
            userService.save(user);
            log.info("新用户注册 -> {}", user);
        }else {
            user.setId(dbUser.getId());
            // 更新一下数据
            userService.lambdaUpdate().eq(User::getOpenid, openid).update(user);
        }
        // 5. 构造 token
        Map<String, Object> tokenData = new HashMap<String, Object>(){{
            this.put(ExtractUtil.OPENID, openid);
            this.put(ExtractUtil.UNIONID, unionid);
//            this.put(ExtractUtil.SESSION_KEY, sessionKey);
        }};
        String jsonData = JsonUtil.analyzeData(tokenData);
        String token = JwtUtil.createJWT(jsonData);
        return new HashMap<String, Object>(){{
            this.put(JwtUtil.JWT_HEADER, token);
        }};
    }

    @Override
    public void logout(HttpServletRequest request) {
        ExtractUtil.joinTheTokenBlacklist(request);
    }
}
