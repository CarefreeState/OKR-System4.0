package cn.lbcmmszdntnt.domain.auth.controller;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.domain.auth.service.EmailIdentifyService;
import cn.lbcmmszdntnt.domain.user.enums.UserType;
import cn.lbcmmszdntnt.domain.user.model.dto.EmailCheckDTO;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 16:53
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "邮箱测试接口")
@Intercept(permit = {UserType.NORMAL_USER, UserType.MANAGER})
@Validated
public class EmailController {

    private final EmailIdentifyService emailIdentifyService;

    @PostMapping("/user/check/email")
    @Operation(summary = "验证邮箱用户")
    @Tag(name = "用户测试接口/邮箱")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<?> emailIdentityCheck(@Valid @RequestBody EmailCheckDTO emailCheckDTO) {
        String email = emailCheckDTO.getEmail();
        emailIdentifyService.sendIdentifyingCode(emailCheckDTO.getType(), email);
        // 能到这一步就成功了
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }
}
