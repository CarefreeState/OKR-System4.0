package cn.lbcmmszdntnt.domain.medal.handler.ext;
import cn.lbcmmszdntnt.domain.medal.handler.ApplyMedalHandler;
import cn.lbcmmszdntnt.domain.medal.handler.util.MedalEntryUtil;
import cn.lbcmmszdntnt.domain.medal.model.entry.ShortTermAchievement;
import cn.lbcmmszdntnt.domain.medal.model.po.UserMedal;
import cn.lbcmmszdntnt.domain.medal.service.UserMedalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:26
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ShortTermAchievementHandler extends ApplyMedalHandler {

    private final static Class<ShortTermAchievement> MEDAL_ENTRY = ShortTermAchievement.class;

    @Value("${medal.short-term-achievement.id}")
    private Long medalId;

    @Value("${medal.short-term-achievement.coefficient}")
    private Integer coefficient;

    private final UserMedalService userMedalService;

    private final Function<Long, Integer> getLevelStrategy = credit -> MedalEntryUtil.getLevel(credit, coefficient);

    public int getIncrement(ShortTermAchievement shortTermAchievement) {
        Boolean isCompleted = shortTermAchievement.getIsCompleted();
        Boolean oldCompleted = shortTermAchievement.getOldCompleted();
        if(Boolean.TRUE.equals(oldCompleted)) {
            return Boolean.TRUE.equals(isCompleted) ? 0 : -1;
        }else {
            return Boolean.TRUE.equals(isCompleted) ? 1 : 0;
        }
    }

    @Override
    public void handle(Object object) {
        log.info("{} 尝试处理对象 {}", this.getClass(), object);
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(shortTermAchievement -> {
            // 任务是否完成，决定是否计数给用户
            Long userId = shortTermAchievement.getUserId();
            UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
            long credit = Objects.isNull(dbUserMedal) ? 0 : dbUserMedal.getCredit();
            int increment = getIncrement(shortTermAchievement);
            if(increment != 0) {
                credit += increment;
                super.saveMedalEntry(userId, medalId, credit, dbUserMedal, getLevelStrategy);
            }
        });
        super.doNextHandler(object);
    }

}
