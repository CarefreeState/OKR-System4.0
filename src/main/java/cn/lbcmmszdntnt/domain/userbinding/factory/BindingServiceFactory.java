package cn.lbcmmszdntnt.domain.userbinding.factory;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.userbinding.enums.BindingType;
import cn.lbcmmszdntnt.domain.userbinding.service.BindingService;
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
