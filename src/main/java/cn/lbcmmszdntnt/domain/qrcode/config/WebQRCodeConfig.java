package cn.lbcmmszdntnt.domain.qrcode.config;

import cn.lbcmmszdntnt.domain.qrcode.model.dto.WebQRCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 16:00
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "qrcode.web")
public class WebQRCodeConfig {

    private WebQRCode invite;

    private WebQRCode login;

}
