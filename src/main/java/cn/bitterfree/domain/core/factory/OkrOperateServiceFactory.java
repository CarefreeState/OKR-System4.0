package cn.bitterfree.domain.core.factory;


import cn.bitterfree.domain.core.enums.OkrType;
import cn.bitterfree.domain.core.service.OkrOperateService;
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
 * Date: 2024-09-04
 * Time: 10:53
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.service.okr-operate-service")
public class OkrOperateServiceFactory {

    private Map<OkrType, String> map;

    public OkrOperateService getService(OkrType okrType) {
        return SpringUtil.getBean(map.get(okrType), OkrOperateService.class);
    }

}
