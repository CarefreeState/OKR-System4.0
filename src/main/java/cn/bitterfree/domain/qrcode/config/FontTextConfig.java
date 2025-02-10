package cn.bitterfree.domain.qrcode.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
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

    private TextColor lineColor;

    private String common;

    private String invite;

    private String binding;

    private String login;

    private Color color;

    @PostConstruct
    public void init() {
        this.color = Optional.ofNullable(color).orElseGet(lineColor::color);
    }

    @Data
    public static class TextColor {

        private Integer red;

        private Integer green;

        private Integer blue;

        public Color color() {
            return new Color(red, green, blue);
        }

    }

}
