package cn.lbcmmszdntnt.domain.qrcode.config;

import cn.lbcmmszdntnt.wxtoken.model.dto.WxQRCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 15:54
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "qrcode.wx")
public class WxQRCodeConfig {

    private WxQRCode common;

    private WxQRCode invite;

    private WxQRCode binding;

    private WxQRCode login;
}
