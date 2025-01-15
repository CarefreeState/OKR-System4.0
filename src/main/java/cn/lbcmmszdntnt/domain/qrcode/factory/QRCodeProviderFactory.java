package cn.lbcmmszdntnt.domain.qrcode.factory;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;
import cn.lbcmmszdntnt.domain.qrcode.provider.QRCodeProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 15:23
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.service.qrcode-provider")
public class QRCodeProviderFactory {

    private Map<QRCodeType, String> map;

    public QRCodeProvider getProvider(QRCodeType qrCodeType) {
        return SpringUtil.getBean(map.get(qrCodeType), QRCodeProvider.class);
    }

}
