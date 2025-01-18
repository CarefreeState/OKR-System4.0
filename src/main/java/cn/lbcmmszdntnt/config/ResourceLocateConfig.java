package cn.lbcmmszdntnt.config;

import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "resource.locate")
public class ResourceLocateConfig implements WebMvcConfigurer {

    private final static String CLASS_PATH = "classpath:";

    private String patten;

    private String location;

    @Resource
    private ResourceStaticConfig resourceStaticConfig;

    /**
     * 配置静态访问资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(patten).addResourceLocations(location);
        registry.addResourceHandler(resourceStaticConfig.getDefaultPhoto()).addResourceLocations(CLASS_PATH + resourceStaticConfig.getDefaultPhoto());
    }

}
