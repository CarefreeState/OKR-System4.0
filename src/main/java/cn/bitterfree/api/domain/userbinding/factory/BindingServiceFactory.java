package cn.bitterfree.api.domain.userbinding.factory;

import cn.bitterfree.api.domain.userbinding.enums.BindingType;
import cn.bitterfree.api.domain.userbinding.service.BindingService;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 22:40
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.service.binding-service")
public class BindingServiceFactory {

    private Map<BindingType, String> map;

    public BindingService getService(BindingType bindingType) {
        return SpringUtil.getBean(map.get(bindingType), BindingService.class);
    }


}
