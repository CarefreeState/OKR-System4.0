package cn.bitterfree.domain.medal.factory;


import cn.bitterfree.domain.core.enums.TaskType;
import cn.bitterfree.domain.medal.service.TermAchievementService;
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
 * Time: 15:46
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.service.team-achievement-service")
public class TeamAchievementServiceFactory {

    private Map<TaskType, String> map;

    public TermAchievementService getService(TaskType taskType) {
        return SpringUtil.getBean(map.get(taskType), TermAchievementService.class);
    }

}
