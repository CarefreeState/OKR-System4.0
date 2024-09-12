package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.common.constants.SuppressWarningsValue;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.email.factory.EmailServiceFactory;
import cn.lbcmmszdntnt.domain.user.model.dto.EmailLoginDTO;
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
 * Date: 2024-01-24
 * Time: 13:18
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings(value = {SuppressWarningsValue.SPRING_JAVA_INJECTION_POINT_AUTOWIRING_INSPECTION})
public class EmailLoginServiceImpl implements LoginService {

    private final static String DEFAULT_NICKNAME = "邮箱用户";

    private final static String DEFAULT_PHOTO = "media/static/default.png";

    private final UserService userService;

    private final EmailServiceFactory emailServiceFactory;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        EmailLoginDTO emailLoginDTO = loginDTO.getEmailLoginDTO();
        if(Objects.isNull(emailLoginDTO)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        String email = emailLoginDTO.getEmail();
        String code = emailLoginDTO.getCode();
        // 验证码验证
        emailServiceFactory
                .getService(EmailServiceFactory.EMAIL_LOGIN)
                .checkIdentifyingCode(email, code);
        User user = emailLoginDTO.transToUser();
        // 如果用户未不存在（邮箱未注册），则注册
        User dbUser = userService.lambdaQuery().eq(User::getEmail, email).one();
        if(Objects.isNull(dbUser)) {
            user.setNickname(DEFAULT_NICKNAME);
            user.setPhoto(DEFAULT_PHOTO);
            userService.save(user);
            log.info("新用户注册 -> {}", user);
        }else {
            user.setId(dbUser.getId());
        }
        // 构造 token
        Map<String, Object> tokenData = new HashMap<String, Object>(){{
            this.put(ExtractUtil.ID, user.getId());
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
