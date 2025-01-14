package cn.lbcmmszdntnt.domain.user.controller;

import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.LoginQRCodeVO;
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
@RequestMapping("/events")
@RequiredArgsConstructor
@Intercept(authenticate = false, authorize = false)
public class SseUserServer {

    public final static String SSE_USER_SERVER = "SseUserServer:";

    private final static long timeout = QRCodeConstants.WX_LOGIN_QR_CODE_UNIT.toMillis(QRCodeConstants.WX_LOGIN_QR_CODE_TTL);

    private final OkrQRCodeService okrQRCodeService;

    @Operation(summary = "网页端微信登录（SSE）")
    @Tag(name = "用户测试接口/微信")
    @GetMapping("/web/wxlogin")
    public SseEmitter connect() {
        // 获得邀请码的密钥
        LoginQRCodeVO loginQRCode = okrQRCodeService.getLoginQRCode();
        // 连接并发送一条信息
        return SseSessionUtil.createConnect(timeout, SSE_USER_SERVER + loginQRCode.getSecret(),
                () -> JsonUtil.analyzeData(loginQRCode));
    }

}
