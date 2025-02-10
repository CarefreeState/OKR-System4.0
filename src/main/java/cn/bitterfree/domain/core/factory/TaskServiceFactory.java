package cn.bitterfree.domain.core.factory;


import cn.bitterfree.domain.core.enums.TaskType;
import cn.bitterfree.domain.core.service.inner.TaskService;
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
 * Time: 16:40
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.service.task-service")
public class TaskServiceFactory {

    private Map<TaskType, String> map;

    public TaskService getService(TaskType taskType) {
        return SpringUtil.getBean(map.get(taskType), TaskService.class);
    }

}
