package cn.lbcmmszdntnt.domain.qrcode.config;

import cn.lbcmmszdntnt.wxtoken.model.dto.LineColor;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "font.text")
public class FontTextConfig {

    private LineColor lineColor;

    private String common;

    private String invite;

    private String binding;

    private String login;

    private Color color;

    @PostConstruct
    public void init() {
        this.color = Optional.ofNullable(color).orElseGet(lineColor::color);
    }
}
