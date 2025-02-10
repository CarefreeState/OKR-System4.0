package cn.bitterfree.domain.record.factory;


import cn.bitterfree.domain.core.enums.TaskType;
import cn.bitterfree.domain.record.service.DayRecordCompleteService;
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
 * Date: 2024-09-05
 * Time: 16:47
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.service.day-record-complete-service")
public class DayaRecordCompleteServiceFactory {

    private Map<TaskType, String> map;

    public DayRecordCompleteService getService(TaskType taskType) {
        return SpringUtil.getBean(map.get(taskType), DayRecordCompleteService.class);
    }

}
