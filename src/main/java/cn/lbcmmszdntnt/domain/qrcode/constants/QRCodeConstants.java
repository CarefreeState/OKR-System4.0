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

    String DEFAULT_ORIGINAL_NAME = "wxqrcode.png";

    String WX_CHECK_QR_CODE_MAP = "wxCheckQRCodeMap:";
    String WX_COMMON_QR_CODE_KEY = "wxCommonQRCodeKey";
    String OKR_COMMON_QR_CODE_LOCK = "okrCommonQRCodeLock";

    Long WX_CHECK_QR_CODE_TTL = 5L;
    Long COMMON_QR_CODE_TTL = 64L;

    TimeUnit WX_CHECK_QR_CODE_UNIT = TimeUnit.MINUTES;
    TimeUnit COMMON_QR_CODE_UNIT = TimeUnit.DAYS;

    String TEAM_QR_CODE_MAP = "teamQRCodeMap:%s:%d";
    Long TEAM_QR_MAP_TTL = 30L;
    TimeUnit TEAM_QR_MAP_UNIT = TimeUnit.DAYS;

    String OKR_INVITE_QR_CODE_LOCK = "okrInviteQRCodeLock:";


    Long LOGIN_QR_CODE_TTL = 1L;
    TimeUnit LOGIN_QR_CODE_UNIT = TimeUnit.MINUTES;
    Long BINDING_CODE_ACTIVE_LIMIT = QRCodeConstants.WX_CHECK_QR_CODE_UNIT.toMillis(QRCodeConstants.WX_CHECK_QR_CODE_TTL);
    Long LOGIN_CODE_ACTIVE_LIMIT = QRCodeConstants.LOGIN_QR_CODE_UNIT.toMillis(QRCodeConstants.LOGIN_QR_CODE_TTL);
    String COMMON_CODE_MESSAGE = "让目标照耀前程，用规划书写人生！";
    String BINDING_CODE_MESSAGE = String.format("请在 %d %s 内前往微信扫码进行绑定！", QRCodeConstants.WX_CHECK_QR_CODE_TTL, QRCodeConstants.WX_CHECK_QR_CODE_UNIT);
    String LOGIN_CODE_MESSAGE = String.format("请在 %d %s 内前往微信扫码进行验证！", QRCodeConstants.LOGIN_QR_CODE_TTL, QRCodeConstants.LOGIN_QR_CODE_UNIT);


}
