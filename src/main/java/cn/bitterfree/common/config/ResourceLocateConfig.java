package cn.bitterfree.common.config;

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

    private String patten;

    private String location;

    /**
     * 配置静态访问资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(patten).addResourceLocations(location);
    }

}
