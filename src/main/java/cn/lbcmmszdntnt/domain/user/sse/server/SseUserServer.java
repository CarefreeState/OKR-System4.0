package cn.lbcmmszdntnt.domain.user.sse.server;

import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.domain.qrcode.config.QRCodeConfig;
import cn.lbcmmszdntnt.domain.qrcode.service.OkrQRCodeService;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.sse.util.SseSessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Tag(name = "用户 SSE 接口")
@RequestMapping("/events")
@RequiredArgsConstructor
@Intercept(authenticate = false, authorize = false)
public class SseUserServer {

    public final static String SSE_USER_SERVER = "SseUserServer:";

    private final static long timeout = QRCodeConfig.WX_LOGIN_QR_CODE_UNIT.toMillis(QRCodeConfig.WX_LOGIN_QR_CODE_TTL);

    private final OkrQRCodeService okrQRCodeService;

    @Operation(summary = "网页端微信登录")
    @GetMapping("/web/wxlogin")
    public SseEmitter connect() {
        // 获得邀请码的密钥
        String secret = okrQRCodeService.getSecretCode();
        // 连接并发送一条信息
        return SseSessionUtil.createConnect(timeout, SSE_USER_SERVER + secret,
                () -> JsonUtil.analyzeData(okrQRCodeService.getLoginQRCode(secret)));
    }

}
