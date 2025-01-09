package cn.lbcmmszdntnt.domain.record.factory;


import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.core.enums.TaskType;
import cn.lbcmmszdntnt.domain.record.service.DayRecordCompleteService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 16:47
 */
@Configuration
@ConfigurationProperties(prefix = "day-record-complete-service")
@Data
public class DayaRecordCompleteServiceFactory {

    private Map<TaskType, String> map;

    public DayRecordCompleteService getService(TaskType taskType) {
        return SpringUtil.getBean(map.get(taskType), DayRecordCompleteService.class);
    }

}
