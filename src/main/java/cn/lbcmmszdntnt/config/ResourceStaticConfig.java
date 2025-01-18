package cn.lbcmmszdntnt.config;

import cn.lbcmmszdntnt.common.util.media.ClassPathResourceUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 17:05
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "resource.static")
public class ResourceStaticConfig {

    private String defaultPhoto;

    private String font;

    private String board;

    private byte[] fontBytes;

    private byte[] boardBytes;

    @PostConstruct
    public void init() {
        this.fontBytes = ClassPathResourceUtil.getBytes(font);
        this.boardBytes = ClassPathResourceUtil.getBytes(board);
    }
}
