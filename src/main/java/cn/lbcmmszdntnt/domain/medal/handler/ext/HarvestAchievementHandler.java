package cn.lbcmmszdntnt.domain.medal.handler.ext;

import cn.lbcmmszdntnt.domain.medal.handler.ApplyMedalHandler;
import cn.lbcmmszdntnt.domain.medal.handler.util.MedalEntryUtil;
import cn.lbcmmszdntnt.domain.medal.model.entry.OkrFinish;
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
 * Time: 12:25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HarvestAchievementHandler extends ApplyMedalHandler {

    private final static Class<OkrFinish> MEDAL_ENTRY = OkrFinish.class;

    @Value("${medal.harvest-achievement.id}")
    private Long medalId;

    @Value("${medal.harvest-achievement.coefficient}")
    private Integer coefficient;

    private final UserMedalService userMedalService;

    private final Function<Long, Integer> getLevelStrategy = credit -> MedalEntryUtil.getLevel(credit, coefficient);

    @Override
    public void handle(Object object) {
        log.info("{} 尝试处理对象 {}", this.getClass(), object);
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(okrFinish -> {
            // 将完成度换算成积分给用户
            Integer degree = okrFinish.getDegree();
            Long userId = okrFinish.getUserId();
            UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
            long credit = Objects.isNull(dbUserMedal) ? degree : dbUserMedal.getCredit() + degree;
            log.info("用户 {} OKR 完成度积分 {}", userId, credit);
            super.saveMedalEntry(userId, medalId, credit, dbUserMedal, getLevelStrategy);
        });
        super.doNextHandler(object);
    }

}
