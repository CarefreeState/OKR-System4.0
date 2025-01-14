package cn.lbcmmszdntnt.domain.qrcode.config;

import cn.lbcmmszdntnt.domain.qrcode.model.dto.WebQRCode;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 16:00
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "web")
public class WebQRCodeConfig {

    private WebQRCode invite;

}
