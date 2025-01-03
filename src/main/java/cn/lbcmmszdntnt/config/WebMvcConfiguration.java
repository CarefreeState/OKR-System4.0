package cn.lbcmmszdntnt.config;

import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    public static String MAP_ROOT;

    public static String ROOT;

    public static String STATIC_PATH;

    public static String INVITE_PATH;

    public static String BINDING_PATH;

    public static String SWAGGER_PATH;

    public static String LOGIN_PATH;

    public static String PHOTO_PATH;

    public static String COMMON_PATH;

    public static String MEDAL_PATH;

    /**
     * 配置静态访问资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + MAP_ROOT + "**")
                .addResourceLocations("file:" + ROOT + MAP_ROOT);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtil.OBJECT_MAPPER;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter()); // 避免 api-docs 编写为 base64 码
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper()));
    }

    @Value("${media.map}")
    private void setMAP_ROOT(String mapRoot) {
        MAP_ROOT = mapRoot;
    }

    @Value("${media.root}")
    private void setROOT(String root) {
        ROOT = root;
    }

    @Value("${media.static}")
    private void setSTATIC_PATH(String staticPath) {
        STATIC_PATH = staticPath;
    }

    @Value("${media.invite}")
    private void setINVITE_PATH(String invitePath) {
        INVITE_PATH = invitePath;
    }

    @Value("${media.binding}")
    private void setBINDING_PATH(String bindingPath) {
        BINDING_PATH = bindingPath;
    }

    @Value("${media.swagger}")
    private void setSWAGGER_PATH(String swaggerPath) {
        SWAGGER_PATH = swaggerPath;
    }

    @Value("${media.login}")
    private void setLOGIN_PATH(String loginPath) {
        LOGIN_PATH = loginPath;
    }

    @Value("${media.photo}")
    private void setPHOTO_PATH(String photoPath) {
        PHOTO_PATH = photoPath;
    }

    @Value("${media.common}")
    private void setCOMMON_PATH(String commonPath) {
        COMMON_PATH = commonPath;
    }

    @Value("${media.medal}")
    private void setMEDAL_PATH(String medalPath) {
        MEDAL_PATH = medalPath;
    }

}
