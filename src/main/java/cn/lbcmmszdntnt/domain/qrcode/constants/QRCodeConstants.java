package cn.lbcmmszdntnt.domain.qrcode.constants;

import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 17:49
 */
public interface QRCodeConstants {

    QRCodeType DEFAULT_QRCODE_TYPE = QRCodeType.WX;

    String DEFAULT_ORIGINAL_NAME = "wxqrcode.png";

    String WX_CHECK_QR_CODE_MAP = "wxCheckQRCodeMap:";

    String WX_CHECK_QR_CODE_CACHE = "wxCheckQRCodeCache:";

    String WX_LOGIN_QR_CODE_MAP = "wxLoginQRCodeMap:";

    String WX_LOGIN_QR_CODE_CACHE = "wxLoginQRCodeCache:";

    String WX_COMMON_QR_CODE_KEY = "wxCommonQRCodeKey";

    String OKR_COMMON_QR_CODE_LOCK = "okrCommonQRCodeLock";

    Long WX_CHECK_QR_CODE_TTL = 5L;

    Long WX_LOGIN_QR_CODE_TTL = 1L;

    Long WX_COMMON_QR_CODE_TTL = 64L;

    TimeUnit WX_CHECK_QR_CODE_UNIT = TimeUnit.MINUTES;

    TimeUnit WX_LOGIN_QR_CODE_UNIT = TimeUnit.MINUTES;

    TimeUnit WX_COMMON_QR_CODE_UNIT = TimeUnit.DAYS;

    String TEAM_QR_CODE_MAP = "teamQRCodeMap:%s:%d";

    Long TEAM_QR_MAP_TTL = 30L;

    TimeUnit TEAM_QR_MAP_UNIT = TimeUnit.DAYS;

    String OKR_INVITE_QR_CODE_LOCK = "okrInviteQRCodeLock:";

}
