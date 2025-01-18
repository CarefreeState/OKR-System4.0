package cn.lbcmmszdntnt.domain.auth.controller;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.domain.auth.constants.AuthConstants;
import cn.lbcmmszdntnt.domain.auth.model.dto.LoginQRCodeDTO;
import cn.lbcmmszdntnt.domain.auth.service.LoginAckIdentifyService;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.LoginQRCodeVO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.interceptor.context.InterceptorContext;
import cn.lbcmmszdntnt.sse.util.SseMessageSender;
import cn.lbcmmszdntnt.sse.util.SseSessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 17:33
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "登录授权")
@Validated
public class LoginAckController {

    private final LoginAckIdentifyService loginAckIdentifyService;

    @Operation(summary = "获取授权登录码（SSE 请求）", description = """
            ## 1. 客户端展示登录码
            
            建立连接后，会返回一个 json（后端会在一分钟后断开连接）
            
            在 1 分钟内用户应该在小程序端扫码登录后，获取场景值（场景值示例：`secret=T1dH_S`）进行下一步操作；
            
            ## 2. 扫码跳转，确认登录
            
            1. 跳转到小程序的路径（如果需要更改请联系开发者）：`pages/confirm`，直接根据微信提供的接口获取场景值就行；
            2. 跳转到网站的路径（如果需要更改请联系开发者）：`${spring.domain}/pages/confirm`，直接获取 queryString 的 scene 参数；
            
            将 `secret=T1dH_S` 的 `T1dH_S` 作为路径参数请求 `/user/login/ack/{secret}` （必须是在登录的状态下）；
            
            服务器会给【刚才获得二维码的客户端】发一个消息（状态码为 200）作为提示；
            
            如果收到此消息，那么就可以断开连接，进行第三步了；
            
            ## 3. 客户端登录
            
            在登录接口，选择授权登录策略，用获取二维码时返回的 secret 去登录；
            """)
    @PostMapping(value = "/sse/login/qrcode")
    @Intercept(authenticate = false, authorize = false)
    @ApiResponse(content = @Content(
            schema = @Schema(implementation = LoginQRCodeVO.class)
    ))
    public SseEmitter getLoginQRCode(@Valid @RequestBody LoginQRCodeDTO loginQRCodeDTO) {
        // 获得邀请码
        String secret = loginAckIdentifyService.getSecret();
        // 连接并发送一条信息
        return SseSessionUtil.createConnect(
                QRCodeConstants.LOGIN_CODE_ACTIVE_LIMIT,
                AuthConstants.LOGIN_ACK_SSE_SERVER + secret,
                () -> loginAckIdentifyService.getLoginQRCode(secret, loginQRCodeDTO.getType())
        );
    }

    @PostMapping("/user/login/ack/{secret}")
    @Operation(summary = "用户授权")
    @Intercept(authenticate = true, authorize = false)
    public SystemJsonResponse<?> loginAck(@PathVariable("secret") @Parameter(description = "secret") String secret) {
        User user = InterceptorContext.getUser();
        loginAckIdentifyService.ackSecret(secret, user.getId());
        // 发送已确认的通知
        SystemJsonResponse<?> systemJsonResponse = SystemJsonResponse.SYSTEM_SUCCESS();
        SseMessageSender.sendMessage(AuthConstants.LOGIN_ACK_SSE_SERVER + secret, systemJsonResponse);
        return systemJsonResponse;
    }

}
