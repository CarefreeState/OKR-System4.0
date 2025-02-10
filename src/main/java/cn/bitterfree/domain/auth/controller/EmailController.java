package cn.bitterfree.domain.auth.controller;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.domain.auth.model.dto.EmailIdentifyDTO;
import cn.bitterfree.domain.auth.service.EmailIdentifyService;
import cn.bitterfree.interceptor.annotation.Intercept;
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
@Tag(name = "邮箱")
@Validated
public class EmailController {

    private final EmailIdentifyService emailIdentifyService;

    // 其实还不一定需要存在这个邮箱，只不过不存在邮箱，除了服务器谁都不知道验证码
    @PostMapping("/user/check/email")
    @Operation(summary = "发送邮箱验证码")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<?> emailIdentityCheck(@Valid @RequestBody EmailIdentifyDTO emailIdentifyDTO) {
        String email = emailIdentifyDTO.getEmail();
        emailIdentifyService.sendIdentifyingCode(emailIdentifyDTO.getType(), email);
        // 能到这一步就成功了
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }
}
