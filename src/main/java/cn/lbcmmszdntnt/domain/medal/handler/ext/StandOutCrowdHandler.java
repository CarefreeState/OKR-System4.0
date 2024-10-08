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
 * Time: 12:26
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StandOutCrowdHandler extends ApplyMedalHandler {

    private final static Class<OkrFinish> MEDAL_ENTRY = OkrFinish.class;

    private final static Integer COMMON_DEGREE_THRESHOLD = 79;

    private final static Integer EXCELLENT_DEGREE_THRESHOLD = 100;

    @Value("${medal.stand-out-crowd.id}")
    private Long medalId;

    @Value("${medal.stand-out-crowd.coefficient}")
    private Integer coefficient;

    private final UserMedalService userMedalService;

    private final Function<Long, Integer> getLevelStrategy = credit -> MedalEntryUtil.getLevel(credit, coefficient);

    private int getStandOutCredit(Boolean isAdvance, Integer degree) {
        int count = 0;
        if(Objects.nonNull(isAdvance) && Boolean.TRUE.equals(isAdvance) && degree.compareTo(COMMON_DEGREE_THRESHOLD) > 0) {
            count++;
        }
        if(Objects.nonNull(degree) && degree.compareTo(EXCELLENT_DEGREE_THRESHOLD) > 0) {
            count++;
        }
        return count;
    }

    @Override
    public void handle(Object object) {
        log.info("{} 尝试处理对象 {}", this.getClass(), object);
        MedalEntryUtil.getMedalEntry(object, MEDAL_ENTRY).ifPresent(okrFinish -> {
            // 截止时间与现在对比，判断是否超额完成，决定是否计数给用户
            Long userId = okrFinish.getUserId();
            Integer degree = okrFinish.getDegree();
            Boolean isAdvance = okrFinish.getIsAdvance();
            int standOutCredit = getStandOutCredit(isAdvance, degree);
            if(standOutCredit > 0) {
                UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
                long credit = Objects.isNull(dbUserMedal) ? standOutCredit : dbUserMedal.getCredit() + standOutCredit;
                super.saveMedalEntry(userId, medalId, credit, dbUserMedal, getLevelStrategy);
            }
        });
        super.doNextHandler(object);
    }

}
