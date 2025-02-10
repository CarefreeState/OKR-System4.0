package cn.bitterfree.domain.medal.listener;

import cn.bitterfree.common.util.convert.ObjectUtil;
import cn.bitterfree.domain.core.constants.FanoutExchangeConstants;
import cn.bitterfree.domain.core.model.message.operate.KeyResultUpdate;
import cn.bitterfree.domain.core.model.message.operate.OkrFinish;
import cn.bitterfree.domain.core.model.message.operate.OkrInitialize;
import cn.bitterfree.domain.core.model.message.operate.TaskUpdate;
import cn.bitterfree.domain.core.model.vo.OkrCoreVO;
import cn.bitterfree.domain.core.model.vo.quadrant.FirstQuadrantVO;
import cn.bitterfree.domain.core.service.OkrCoreService;
import cn.bitterfree.domain.medal.enums.MedalType;
import cn.bitterfree.domain.medal.factory.TeamAchievementServiceFactory;
import cn.bitterfree.domain.medal.model.entity.UserMedal;
import cn.bitterfree.domain.medal.service.TermAchievementService;
import cn.bitterfree.domain.medal.service.UserMedalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

import static cn.bitterfree.domain.medal.constants.MedalConstants.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 21:16
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OkrMedalMessageListener {

    private final UserMedalService userMedalService;

    private final TeamAchievementServiceFactory teamAchievementServiceFactory;

    private final OkrCoreService okrCoreService;

    // 胜券在握（信心指数）
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = FanoutExchangeConstants.KEY_RESULT_UPDATE_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(name = FanoutExchangeConstants.KEY_RESULT_UPDATE_MEDAL_QUEUE, arguments = @Argument(name = "x-queue-mode", value = "lazy"))
    ))
    public void victoryWithinGraspMedalMessageListener(KeyResultUpdate keyResultUpdate) {

        Integer probability = keyResultUpdate.getProbability();
        Integer oldProbability = keyResultUpdate.getOldProbability();
        MedalType medalType = MedalType.VICTORY_WITHIN_GRASP;
        // 看看信心指数是否拉满，决定是否计数给用户
        Long userId = keyResultUpdate.getUserId();
        Long medalId = medalType.getMedalId();
        UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
        long credit = Objects.isNull(dbUserMedal) ? 0 : dbUserMedal.getCredit();
        int increment = oldProbability.equals(KEY_RESULT_FULL_VALUE) ? (probability.equals(KEY_RESULT_FULL_VALUE) ? 0 : -1) : (probability.equals(KEY_RESULT_FULL_VALUE) ? 1 : 0);
        if(increment != 0) {
            credit += increment;
            userMedalService.saveUserMedal(userId, medalId, dbUserMedal, credit, medalType.getCoefficient());
        }
    }

    // 短期达标、长久有成（任务完成）
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = FanoutExchangeConstants.TASK_UPDATE_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(name = FanoutExchangeConstants.TASK_UPDATE_MEDAL_QUEUE, arguments = @Argument(name = "x-queue-mode", value = "lazy"))
    ))
    public void termAchievementMedalMessageListener(TaskUpdate taskUpdate) {
        Long userId = taskUpdate.getUserId();
        Boolean isCompleted = taskUpdate.getIsCompleted();
        Boolean oldCompleted = taskUpdate.getOldCompleted();
        // 获得任务对应的勋章类型
        TermAchievementService termAchievementService = teamAchievementServiceFactory.getService(taskUpdate.getTaskType());
        MedalType medalType = termAchievementService.getMedalType();
        // 任务是否完成，决定是否计数给用户
        Long medalId = medalType.getMedalId();
        UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
        long credit = Objects.isNull(dbUserMedal) ? 0 : dbUserMedal.getCredit();
        int increment = Boolean.TRUE.equals(oldCompleted) ? (Boolean.TRUE.equals(isCompleted) ? 0 : -1) : (Boolean.TRUE.equals(isCompleted) ? 1 : 0);
        if(increment != 0) {
            credit += increment;
            userMedalService.saveUserMedal(userId, medalId, dbUserMedal, credit, medalType.getCoefficient());
        }
    }

    // 出类拔萃（超额、提前完成 OKR）
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = FanoutExchangeConstants.OKR_FINISH_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(name = FanoutExchangeConstants.OKR_STANDOUT_MEDAL_QUEUE)
    ))
    public void standOutCrowdMedalMessageListener(OkrFinish okrFinish) {
        // 截止时间与现在对比，判断是否超额完成，决定是否计数给用户
        Long userId = okrFinish.getUserId();
        Integer degree = okrFinish.getDegree();
        Boolean isAdvance = okrFinish.getIsAdvance();
        int standOutCredit = 0;
        if(Objects.nonNull(isAdvance) && Boolean.TRUE.equals(isAdvance) && degree.compareTo(COMMON_DEGREE_THRESHOLD) >= 0) {
            standOutCredit++;
        }
        if(Objects.nonNull(degree) && degree.compareTo(EXCELLENT_DEGREE_THRESHOLD) > 0) {
            standOutCredit++;
        }
        if(standOutCredit > 0) {
            MedalType medalType = MedalType.STAND_OUT_CROWD;
            Long medalId = medalType.getMedalId();
            UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
            long credit = Objects.isNull(dbUserMedal) ? standOutCredit : (dbUserMedal.getCredit() + standOutCredit);
            userMedalService.saveUserMedal(userId, medalId, dbUserMedal, credit, medalType.getCoefficient());
        }
    }

    // 硕果累累（持续完成 OKR 的情况）
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = FanoutExchangeConstants.OKR_FINISH_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(name = FanoutExchangeConstants.OKR_HARVEST_MEDAL_QUEUE)
    ))
    public void harvestAchievementMedalMessageListener(OkrFinish okrFinish) {
        MedalType medalType = MedalType.HARVEST_ACHIEVEMENT;
        Long medalId = medalType.getMedalId();
        // 将完成度换算成积分给用户
        Integer degree = okrFinish.getDegree();
        Long userId = okrFinish.getUserId();
        UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
        long credit = Objects.isNull(dbUserMedal) ? degree : dbUserMedal.getCredit() + degree;
        log.info("用户 {} OKR 完成度积分 {}", userId, credit);
        userMedalService.saveUserMedal(userId, medalId, dbUserMedal, credit, medalType.getCoefficient());
    }

    // 初心启航（第一次制定一个 OKR）
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = FanoutExchangeConstants.OKR_INITIALIZE_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(name = FanoutExchangeConstants.OKR_INITIALIZE_MEDAL_QUEUE)
    ))
    public void stayTrueBeginningMedalMessageListener(OkrInitialize okrInitialize) {
        MedalType medalType = MedalType.STAY_TRUE_BEGINNING;
        Long medalId = medalType.getMedalId();
        // 判断是否是第一次指定 OKR
        Long userId = okrInitialize.getUserId();
        UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
        if(Objects.isNull(dbUserMedal)) {
            Long coreId = okrInitialize.getCoreId();
            OkrCoreVO okrCoreVO = okrCoreService.searchOkrCore(coreId);
            FirstQuadrantVO firstQuadrantVO = okrCoreVO.getFirstQuadrantVO();
            String objective = firstQuadrantVO.getObjective();
            Date firstQuadrantDeadline = firstQuadrantVO.getDeadline();
            Integer secondQuadrantCycle = okrCoreVO.getSecondQuadrantCycle();
            Date secondQuadrantDeadline = okrCoreVO.getSecondQuadrantVO().getDeadline();
            Integer thirdQuadrantCycle = okrCoreVO.getThirdQuadrantCycle();
            Date thirdQuadrantDeadline = okrCoreVO.getThirdQuadrantVO().getDeadline();
            boolean flag = ObjectUtil.noneIsNull(objective, firstQuadrantDeadline, secondQuadrantCycle,
                    secondQuadrantDeadline, thirdQuadrantCycle, thirdQuadrantDeadline);
            if(Boolean.TRUE.equals(flag)) {
                userMedalService.saveUserMedal(userId, medalId, null, 1L, medalType.getCoefficient());
            }
        }
    }

}
