package cn.bitterfree.api.domain.auth.generator;

import cn.bitterfree.api.common.util.convert.UUIDUtil;
import cn.bitterfree.api.domain.auth.constants.AuthConstants;
import cn.bitterfree.api.domain.qrcode.constants.QRCodeConstants;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.shortcode.ShortCodeGenerator;
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
