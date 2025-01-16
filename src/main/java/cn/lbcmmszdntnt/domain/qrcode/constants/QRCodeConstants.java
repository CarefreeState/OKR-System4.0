package cn.lbcmmszdntnt.domain.qrcode.constants;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 17:49
 */
public interface QRCodeConstants {

    String DEFAULT_ORIGINAL_NAME = "qrcode.png";

    String LOGIN_CODE_SCENE_FORMAT = "secret=%s";
    String INVITE_CODE_SCENE_FORMAT = "teamId=%d&secret=%s";
    String BINDING_CODE_SCENE_FORMAT = "secret=%s";

    Long LOGIN_QR_CODE_TTL = 1L;
    TimeUnit LOGIN_QR_CODE_UNIT = TimeUnit.MINUTES;
    Long LOGIN_CODE_ACTIVE_LIMIT = QRCodeConstants.LOGIN_QR_CODE_UNIT.toMillis(QRCodeConstants.LOGIN_QR_CODE_TTL);
    String LOGIN_CODE_MESSAGE = String.format("请在 %d %s 内前往微信扫码进行验证！", QRCodeConstants.LOGIN_QR_CODE_TTL, QRCodeConstants.LOGIN_QR_CODE_UNIT);

    Long WX_BINDING_QR_CODE_TTL = 5L;
    TimeUnit WX_BINDING_QR_CODE_UNIT = TimeUnit.MINUTES;
    Long BINDING_CODE_ACTIVE_LIMIT = QRCodeConstants.WX_BINDING_QR_CODE_UNIT.toMillis(QRCodeConstants.WX_BINDING_QR_CODE_TTL);
    String BINDING_CODE_MESSAGE = String.format("请在 %d %s 内前往微信扫码进行绑定！", QRCodeConstants.WX_BINDING_QR_CODE_TTL, QRCodeConstants.WX_BINDING_QR_CODE_UNIT);

    String WX_COMMON_QR_CODE_KEY = "wxCommonQRCodeKey";
    String OKR_COMMON_QR_CODE_LOCK = "okrCommonQRCodeLock";
    Long COMMON_QR_CODE_TTL = 64L;
    TimeUnit COMMON_QR_CODE_UNIT = TimeUnit.DAYS;
    String COMMON_CODE_MESSAGE = "让目标照耀前程，用规划书写人生！";

    String TEAM_INVITE_QR_CODE_MAP = "teamInviteQRCodeMap:%s:%d";
    String OKR_INVITE_QR_CODE_LOCK = "okrInviteQRCodeLock:";
    Long TEAM_INVITE_QR_MAP_TTL = 30L;
    TimeUnit TEAM_INVITE_QR_MAP_UNIT = TimeUnit.DAYS;


}
