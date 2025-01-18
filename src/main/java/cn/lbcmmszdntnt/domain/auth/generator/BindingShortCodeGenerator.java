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
 * Date: 2025-01-16
 * Time: 23:19
 */
@Component
public class BindingShortCodeGenerator extends ShortCodeGenerator {

    private final RedisCache redisCache;

    @Override
    public String getOriginCode(String baseStr, String key) {
        return baseStr + key + UUIDUtil.uuid36();
    }

    @Override
    public boolean contains(String code, String key) {
        return redisCache.isExists(AuthConstants.WX_BINDING_QR_CODE_MAP + code);
    }

    @Override
    public void add(String code, String key) {
        redisCache.setObject(AuthConstants.LOGIN_QR_CODE_MAP + code, "null",
                QRCodeConstants.WX_BINDING_QR_CODE_TTL, QRCodeConstants.WX_BINDING_QR_CODE_UNIT);
    }

    public BindingShortCodeGenerator(final RedisCache redisCache, final BindingShortCodeProperties shortCodeProperties) {
        super(shortCodeProperties);
        this.redisCache = redisCache;
    }

}
