package cn.lbcmmszdntnt.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class OpenApiConfig implements WebMvcConfigurer {

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.application.version}")
    private String version;

    @Value("${spring.domain}")
    private String domain;

    @Bean
    public OpenAPI springOpenAPI() {
        OpenAPI info = new OpenAPI()
                .info(new Info()
                .title(name)
                .version(version)
        );
        log.info("{} {} 接口文档路径： ", name, version);
        log.info("index: {}/swagger-ui/index.html", domain);
        log.info("json: {}/v3/api-docs", domain);
        return info;
    }

}