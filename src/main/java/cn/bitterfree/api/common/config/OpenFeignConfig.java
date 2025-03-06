package cn.bitterfree.api.common.config;

import feign.Contract;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-03-05
 * Time: 17:10
 */
@Configuration
public class OpenFeignConfig {

    @Bean
    public Contract notdecodeSlashContract(){
        // 无自定义处理器、默认的 ConversionService、取消 %2F -> / 的解码
        return new SpringMvcContract(Collections.emptyList(), new DefaultConversionService(), Boolean.FALSE);
    }

}
