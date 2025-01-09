package cn.lbcmmszdntnt.domain.qrcode.factory;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;
import cn.lbcmmszdntnt.domain.qrcode.service.InviteQRCodeService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 15:22
 */
@Configuration
@ConfigurationProperties(prefix = "okr.service.invite-qrcode-service")
@Data
public class InviteQRCodeServiceFactory {

    private Map<QRCodeType, String> map;

    public InviteQRCodeService getService(QRCodeType qrCodeType) {
        return SpringUtil.getBean(map.get(qrCodeType), InviteQRCodeService.class);
    }

}
