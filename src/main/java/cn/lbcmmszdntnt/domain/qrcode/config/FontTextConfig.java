package cn.lbcmmszdntnt.domain.qrcode.config;

import cn.lbcmmszdntnt.wxtoken.model.dto.LineColor;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.util.Optional;

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
public class FontTextConfig implements InitializingBean {

    private LineColor lineColor;

    private String common;

    private String invite;

    private String binding;

    private String login;

    private Color color;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.color = Optional.ofNullable(color).orElseGet(lineColor::color);
    }
}
