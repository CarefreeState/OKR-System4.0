package cn.bitterfree.domain.login.controller;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.domain.auth.constants.AuthConstants;
import cn.bitterfree.domain.login.enums.LoginType;
import cn.bitterfree.domain.login.factory.LoginServiceFactory;
import cn.bitterfree.domain.login.model.dto.LoginDTO;
import cn.bitterfree.domain.login.model.vo.LoginVO;
import cn.bitterfree.domain.login.service.LoginService;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.service.UserService;
import cn.bitterfree.interceptor.annotation.Intercept;
import cn.bitterfree.interceptor.jwt.TokenVO;
import cn.bitterfree.jwt.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 16:41
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "登录")
@Validated
public class LoginController {

    private final LoginServiceFactory loginServiceFactory;

    private final UserService userService;

    @PostMapping("/user/login")
    @Operation(summary = "用户登录")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<LoginVO> login(
            @RequestHeader(AuthConstants.LOGIN_HEADER) @Parameter(example = "Rl0p0r", schema = @Schema(
                    type = "string",
                    description = "登录类型 Rl0p0r 邮箱登录、r6Vsr0 微信登录、1eXBrJ 授权登录、jOKQE5 密码登录",
                    allowableValues = {"Rl0p0r", "r6Vsr0", "1eXBrJ", "jOKQE5"}
            )) @NotBlank(message = "登录类型不能为空") String type,
            @Valid @RequestBody LoginDTO loginDTO
    ) {
        // 选取服务
        LoginService loginService = loginServiceFactory.getService(LoginType.get(type));
        User user = loginService.login(loginDTO);
        Long userId = user.getId();
        userService.clearUserAllCache(userId);
        // 构造 token
        TokenVO tokenVO = TokenVO.builder().userId(userId).build();
        String token = JwtUtil.createJwt(AuthConstants.JWT_SUBJECT, tokenVO);
        LoginVO loginVO = LoginVO.builder().token(token).build();
        return SystemJsonResponse.SYSTEM_SUCCESS(loginVO);
    }

}
