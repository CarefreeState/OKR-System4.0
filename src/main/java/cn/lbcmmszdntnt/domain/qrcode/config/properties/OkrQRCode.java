package cn.lbcmmszdntnt.domain.qrcode.config.properties;

import cn.lbcmmszdntnt.util.media.ImageUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-31
 * Time: 1:26
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "font.text")
public class OkrQRCode {

    private Map<String, Integer> color;

    private String common;

    private String invite;

    private String binding;

    private String login;

    private Color textColor;

    @PostConstruct
    public void doPostConstruct() {
        textColor = ImageUtil.getColorByMap(color);
    }

}
