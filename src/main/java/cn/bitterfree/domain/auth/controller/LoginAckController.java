package cn.bitterfree.domain.auth.controller;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.domain.auth.constants.AuthConstants;
import cn.bitterfree.domain.auth.service.LoginAckIdentifyService;
import cn.bitterfree.domain.qrcode.constants.QRCodeConstants;
import cn.bitterfree.domain.qrcode.enums.QRCodeType;
import cn.bitterfree.domain.qrcode.model.vo.LoginQRCodeVO;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.interceptor.annotation.Intercept;
import cn.bitterfree.interceptor.context.InterceptorContext;
import cn.bitterfree.sse.annotation.SseRequest;
import cn.bitterfree.sse.util.SseMessageSender;
import cn.bitterfree.sse.util.SseSessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/user/login")
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
    @SseRequest
    @GetMapping(value = "/qrcode")
    @Intercept(authenticate = false, authorize = false)
    @ApiResponse(content = @Content(
            schema = @Schema(implementation = LoginQRCodeVO.class)
    ))
    public SseEmitter getLoginQRCode(@RequestParam("type") @Parameter(example = "wx", schema = @Schema(
                    type = "string",
                    description = "二维码类型 wx 微信小程序二维码、web 网页二维码",
                    allowableValues = {"wx", "web"}
            )) @NotBlank(message = "二维码类型不能为空") String type) {
        // 获取登录码类型
        QRCodeType qrCodeType = QRCodeType.get(type);
        // 获得登录码
        String secret = loginAckIdentifyService.getSecret();
        String sessionKey = AuthConstants.LOGIN_ACK_SSE_SERVER + secret;
        // 连接并发送一条信息
        SseEmitter sseEmitter = SseSessionUtil.createConnect(QRCodeConstants.LOGIN_CODE_ACTIVE_LIMIT, sessionKey);
        SseMessageSender.sendMessage(sessionKey, () -> loginAckIdentifyService.getLoginQRCode(secret, qrCodeType));
        return sseEmitter;
    }

    @PostMapping("/ack/{secret}")
    @Operation(summary = "用户授权登录")
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
