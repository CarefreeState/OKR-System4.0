package cn.lbcmmszdntnt.domain.auth.generator;

import cn.lbcmmszdntnt.common.util.convert.UUIDUtil;
import cn.lbcmmszdntnt.domain.auth.constants.AuthConstants;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.shortcode.ShortCodeGenerator;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 18:59
 */
@Component
public class LoginShortCodeGenerator extends ShortCodeGenerator {

    private final RedisCache redisCache;

    @Override
    public String getOriginCode(String baseStr, String key) {
        return baseStr + key + UUIDUtil.uuid36();
    }

    @Override
    public boolean contains(String code, String key) {
        return redisCache.isExists(AuthConstants.LOGIN_QR_CODE_MAP + code);
    }

    @Override
    public void add(String code, String key) {
        redisCache.setObject(AuthConstants.LOGIN_QR_CODE_MAP + code, -1,
                QRCodeConstants.LOGIN_QR_CODE_TTL, QRCodeConstants.LOGIN_QR_CODE_UNIT);
    }

    public LoginShortCodeGenerator(final RedisCache redisCache, final LoginShortCodeProperties shortCodeProperties) {
        super(shortCodeProperties);
        this.redisCache = redisCache;
    }
}
