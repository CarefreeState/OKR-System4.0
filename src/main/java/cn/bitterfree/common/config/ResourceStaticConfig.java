package cn.bitterfree.common.config;

import cn.bitterfree.common.util.media.ClassPathResourceUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
public class ResourceStaticConfig  implements WebMvcConfigurer {

    private String patten;

    private String location;

    private String font;

    private String board;

    private byte[] fontBytes;

    private byte[] boardBytes;

    @PostConstruct
    public void init() {
        this.fontBytes = ClassPathResourceUtil.getBytes(font);
        this.boardBytes = ClassPathResourceUtil.getBytes(board);
    }

    /**
     * 配置静态访问资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(patten).addResourceLocations(location);
    }
}
