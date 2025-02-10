package cn.bitterfree.common.config;

import jakarta.servlet.MultipartConfigElement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-23
 * Time: 15:46
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "resource.upload")
public class ResourceUploadConfig {

    private Long maxFileSize;

    private Long maxRequestSize;

    private DataUnit dataUnit;

    /**
     * 配置上传文件大小的配置
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  设置单个文件大小
        factory.setMaxFileSize(DataSize.of(maxFileSize,  dataUnit));
        // 设置总上传文件大小
        factory.setMaxRequestSize(DataSize.of(maxRequestSize, dataUnit));
        // 设置临时存储位置（资源过大不直接写入内存），若进行设置请书写对应的异常处理器
//        factory.setFileSizeThreshold(DataSize.ofMegabytes());
//        factory.setLocation();
        return factory.createMultipartConfig();
    }

}
