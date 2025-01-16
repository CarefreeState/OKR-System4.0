package cn.lbcmmszdntnt.domain.login.controller;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.domain.auth.constants.AuthConstants;
import cn.lbcmmszdntnt.domain.auth.enums.LoginType;
import cn.lbcmmszdntnt.domain.auth.factory.LoginServiceFactory;
import cn.lbcmmszdntnt.domain.login.model.dto.LoginDTO;
import cn.lbcmmszdntnt.domain.login.model.vo.LoginVO;
import cn.lbcmmszdntnt.domain.login.service.LoginService;
import cn.lbcmmszdntnt.domain.user.enums.UserType;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.interceptor.jwt.TokenVO;
import cn.lbcmmszdntnt.jwt.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 16:41
 */
@RestController
@RequiredArgsConstructor
@Intercept(permit = {UserType.NORMAL_USER, UserType.MANAGER})
@RequestMapping("/user/login")
@Tag(name = "登录测试接口")
@Validated
public class LoginController {

    private final LoginServiceFactory loginServiceFactory;

    private final UserService userService;

    @PostMapping({"/", ""})
    @Operation(summary = "用户登录")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<LoginVO> login(
            @RequestHeader(AuthConstants.LOGIN_HEADER) @Parameter(example = "Rl0p0r", schema = @Schema(
                    type = "string",
                    description = "登录类型 Rl0p0r 邮箱登录、r6Vsr0 微信登录、1eXBrJ 授权登录、jOKQE5 密码登录",
                    allowableValues = {"Rl0p0r", "r6Vsr0", "1eXBrJ", "jOKQE5"}
            )) String type,
            @Valid @RequestBody LoginDTO loginDTO
    ) {
        // 选取服务
        LoginService loginService = loginServiceFactory.getService(LoginType.get(type));
        User user = loginService.login(loginDTO);
        Long userId = user.getId();
        userService.deleteUserAllCache(userId);
        // 构造 token
        TokenVO tokenVO = TokenVO.builder().userId(userId).build();
        String token = JwtUtil.createJwt(AuthConstants.JWT_SUBJECT, tokenVO);
        LoginVO loginVO = LoginVO.builder().token(token).build();
        return SystemJsonResponse.SYSTEM_SUCCESS(loginVO);
    }

}
