package cn.lbcmmszdntnt.domain.qrcode.factory;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;
import cn.lbcmmszdntnt.domain.qrcode.provider.QRCodeProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 15:23
 */
@Component
@ConfigurationProperties(prefix = "okr.service.qrcode-provider")
@Data
public class QRCodeProviderFactory {

    private Map<QRCodeType, String> map;

    public QRCodeProvider getProvider(QRCodeType qrCodeType) {
        return SpringUtil.getBean(map.get(qrCodeType), QRCodeProvider.class);
    }

}
