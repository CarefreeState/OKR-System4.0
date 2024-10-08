package cn.lbcmmszdntnt.domain.qrcode.config;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 17:49
 */
public class QRCodeConfig {

    public final static String WX_QR_CORE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";

    public final static String WX_CHECK_QR_CODE_MAP = "wxCheckQRCodeMap:";

    public final static String WX_CHECK_QR_CODE_CACHE = "wxCheckQRCodeCache:";

    public final static String WX_LOGIN_QR_CODE_MAP = "wxLoginQRCodeMap:";

    public final static String WX_LOGIN_QR_CODE_CACHE = "wxLoginQRCodeCache:";

    public final static String WX_COMMON_QR_CODE_KEY = "wxCommonQRCodeKey";

    public final static String OKR_COMMON_QR_CODE_LOCK = "okrCommonQRCodeLock";

    public final static Long WX_CHECK_QR_CODE_TTL = 5L;

    public final static Long WX_LOGIN_QR_CODE_TTL = 1L;

    public final static Long WX_COMMON_QR_CODE_TTL = 64L;

    public final static TimeUnit WX_CHECK_QR_CODE_UNIT = TimeUnit.MINUTES;

    public final static TimeUnit WX_LOGIN_QR_CODE_UNIT = TimeUnit.MINUTES;

    public final static TimeUnit WX_COMMON_QR_CODE_UNIT = TimeUnit.DAYS;

    public final static String TEAM_QR_CODE_MAP = "teamQRCodeMap:%s:%d";

    public final static Long TEAM_QR_MAP_TTL = 30L;

    public final static TimeUnit TEAM_QR_MAP_UNIT = TimeUnit.DAYS;

    public final static String OKR_INVITE_QR_CODE_LOCK = "okrInviteQRCodeLock:";

}
