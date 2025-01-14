package cn.lbcmmszdntnt.domain.user.controller;


import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.common.util.thread.pool.SchedulerThreadPool;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.LoginQRCodeVO;
import cn.lbcmmszdntnt.domain.qrcode.service.OkrQRCodeService;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;
import cn.lbcmmszdntnt.websocket.session.WsSessionMapper;
import cn.lbcmmszdntnt.websocket.util.WsMessageSender;
import cn.lbcmmszdntnt.websocket.util.WsSessionUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@ServerEndpoint("/web/wxlogin")
@Slf4j
@Component
@Intercept(authenticate = false, authorize = false)
public class WsUserServer {

    private String secret;

    public final static String WEB_SOCKET_USER_SERVER = "WebWxLoginServer:";

    private final static OkrQRCodeService OKR_QR_CODE_SERVICE = SpringUtil.getBean(OkrQRCodeService.class);

    @OnOpen
    public void onOpen(Session session) throws DeploymentException {
        SchedulerThreadPool.schedule(() -> {
            WsSessionUtil.close(session);
        }, QRCodeConstants.WX_LOGIN_QR_CODE_TTL, QRCodeConstants.WX_LOGIN_QR_CODE_UNIT);
        // 获得邀请码
        LoginQRCodeVO loginQRCode = OKR_QR_CODE_SERVICE.getLoginQRCode();
        // 获得在 Redis 的键
        secret = loginQRCode.getSecret();
        String sessionKey = WEB_SOCKET_USER_SERVER + secret;
        if (WsSessionMapper.containsKey(sessionKey)) {
            WsSessionMapper.remove(sessionKey);
        }
        WsSessionMapper.put(sessionKey, session);
        // 发送：path, secret
        WsMessageSender.sendMessage(session, loginQRCode);
//        SessionUtil.refuse("拒绝连接");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到消息： {}", message);
    }

    // 成功或者失败的断开都会调用这个方法
    @OnClose
    public void onClose(Session session) {
        String sessionKey = WEB_SOCKET_USER_SERVER + secret;
        log.warn("{} 断开连接", sessionKey);
        WsSessionMapper.remove(sessionKey);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        // 抛异常会在这里被捕获，或者再次抛出，都不会是全局处理器处理
        log.warn("{} 连接出现错误 {}", WEB_SOCKET_USER_SERVER + secret, error.getMessage());
    }

}