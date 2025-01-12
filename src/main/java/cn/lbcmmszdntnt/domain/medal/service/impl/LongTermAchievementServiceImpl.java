package cn.lbcmmszdntnt.domain.medal.service.impl;


import cn.lbcmmszdntnt.domain.medal.enums.MedalType;
import cn.lbcmmszdntnt.domain.medal.model.entity.UserMedal;
import cn.lbcmmszdntnt.domain.medal.service.TermAchievementService;
import cn.lbcmmszdntnt.domain.medal.service.UserMedalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 23:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LongTermAchievementServiceImpl implements TermAchievementService {

    private final UserMedalService userMedalService;

    @Override
    public void handle(Long userId, Boolean isCompleted, Boolean oldCompleted) {
        // 任务是否完成，决定是否计数给用户
        MedalType medalType = MedalType.LONG_TERM_ACHIEVEMENT;
        Long medalId = medalType.getMedalId();
        UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
        long credit = Objects.isNull(dbUserMedal) ? 0 : dbUserMedal.getCredit();
        int increment = Boolean.TRUE.equals(oldCompleted) ? (Boolean.TRUE.equals(isCompleted) ? 0 : -1) : (Boolean.TRUE.equals(isCompleted) ? 1 : 0);
        if(increment != 0) {
            credit += increment;
            userMedalService.saveUserMedal(userId, medalId, dbUserMedal, credit, medalType.getCoefficient());
        }
    }

}
