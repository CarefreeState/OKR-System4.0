package cn.bitterfree.api.common.config;

import cn.bitterfree.api.common.util.convert.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 21:30
 */
@Configuration
public class ObjectMapperConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtil.OBJECT_MAPPER;
    }

    @Bean
    public HttpMessageConverter<?> httpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper());
        converter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                new MediaType("application", "*+json"),
                MediaType.TEXT_PLAIN
        )); // 支持 json 相关类型，多支持一个 text/plain
        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter()); // 避免 api-docs 编写为 base64 码
        converters.add(httpMessageConverter());
    }

}
