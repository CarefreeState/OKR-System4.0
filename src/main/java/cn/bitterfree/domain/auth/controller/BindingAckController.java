package cn.bitterfree.domain.auth.controller;

import cn.bitterfree.common.SystemJsonResponse;
import cn.bitterfree.domain.auth.constants.AuthConstants;
import cn.bitterfree.domain.auth.service.BindingAckIdentifyService;
import cn.bitterfree.domain.qrcode.constants.QRCodeConstants;
import cn.bitterfree.domain.qrcode.model.vo.BindingQRCodeVO;
import cn.bitterfree.interceptor.annotation.Intercept;
import cn.bitterfree.sse.annotation.SseRequest;
import cn.bitterfree.sse.util.SseMessageSender;
import cn.bitterfree.sse.util.SseSessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 23:12
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "绑定授权")
@Validated
@RequestMapping("/user/binding")
public class BindingAckController {

    private final BindingAckIdentifyService bindingAckIdentifyService;

    @Operation(summary = "获取授权绑定码（SSE 请求）", description = """
            ## 1. 客户端展示绑定码
                        
            建立连接后，会返回一个 json（后端会在一分钟后断开连接）
                        
            在 1 分钟内用户应该在小程序端扫码登录后，获取场景值（场景值示例：`secret=T1dH_S`）进行下一步操作；
                        
            ## 2. 扫码跳转，确认绑定
                        
            跳转到小程序的路径（如果需要更改请联系开发者）：`pages/binding`，直接根据微信提供的接口获取场景值就行；
                        
            将 `secret=T1dH_S` 作为路径参数请求 `/user/binding/ack/{secret}/{code}`；
                        
            服务器会给【刚才获得小程序码的客户端】发一个消息（状态码为 200）作为提示；
                        
            如果收到此消息，那么就可以断开连接，进行第三步了；
                        
            ## 3. 客户端绑定
                        
            在绑定接口，选择微信绑定策略，用获取二维码时返回的 secret 去绑定；
            """)
    @SseRequest
    @GetMapping(value = "/qrcode")
    @Intercept(authenticate = false, authorize = false)
    @ApiResponse(content = @Content(schema = @Schema(implementation = BindingQRCodeVO.class)))
    public SseEmitter getBindingQRCode() {
        // 获得绑定码
        String secret = bindingAckIdentifyService.getSecret();
        String sessionKey = AuthConstants.BINDING_ACK_SSE_SERVER + secret;
        // 连接并发送一条信息
        SseEmitter sseEmitter = SseSessionUtil.createConnect(QRCodeConstants.BINDING_CODE_ACTIVE_LIMIT, sessionKey);
        SseMessageSender.sendMessage(sessionKey, () -> bindingAckIdentifyService.getBindingQRCode(secret));
        return sseEmitter;
    }

    @PostMapping("/ack/{secret}/{code}")
    @Operation(summary = "用户授权绑定")
    @Intercept(authenticate = false, authorize = false)
    public SystemJsonResponse<?> bindingAck(@PathVariable("secret") @Parameter(description = "secret") String secret,
                                            @PathVariable("code") @Parameter(description = "code") String code) {
        bindingAckIdentifyService.ackSecret(secret, code);
        // 发送已确认的通知
        SystemJsonResponse<?> systemJsonResponse = SystemJsonResponse.SYSTEM_SUCCESS();
        SseMessageSender.sendMessage(AuthConstants.BINDING_ACK_SSE_SERVER + secret, systemJsonResponse);
        return systemJsonResponse;
    }

}
