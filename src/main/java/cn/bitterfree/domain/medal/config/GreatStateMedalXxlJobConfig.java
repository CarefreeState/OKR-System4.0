package cn.bitterfree.domain.medal.config;


import cn.bitterfree.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.domain.core.config.StatusFlagConfig;
import cn.bitterfree.domain.medal.enums.MedalType;
import cn.bitterfree.domain.medal.model.entity.UserMedal;
import cn.bitterfree.domain.medal.service.UserMedalService;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.service.UserService;
import cn.bitterfree.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 16:19
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class GreatStateMedalXxlJobConfig {

    private final static String ROUTE = "ROUND";
    private final static int TRIGGER_STATUS = 1;
    private final static String CRON = "59 59 23 ? * 1 *"; // 每周日结束前一刻

    private final StatusFlagConfig statusFlagConfig;

    private final UserService userService;

    private final UserMedalService userMedalService;

    // 渐入佳境勋章
    @XxlJob(value = "issueGreatStateMedal")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE, triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】每周一次的渐入佳境勋章检查")
    public void issueGreatStateMedal() {
        List<Long> userIds = userService.lambdaQuery()
                .select(User::getId)
                .list()
                .stream()
                .map(User::getId)
                .toList();
        // 数据量很大，需要分批处理
        IOThreadPool.operateBatch(userIds, ids -> {
            MedalType medalType = MedalType.GREAT_STATE;
            Long medalId = medalType.getMedalId();
            // 查看用户当前未完成的个人 OKR 的所有状态指标，算加权平均值
            statusFlagConfig.calculateStatusFlag(ids).forEach((userId, average) -> {
                // 判断是否计数
                log.info("用户 {} 状态指标评估： {}", userId, average);
                if (statusFlagConfig.isTouch(average)) {
                    UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
                    long credit = Objects.isNull(dbUserMedal) ? 1 : dbUserMedal.getCredit() + 1;
                    userMedalService.saveUserMedal(userId, medalId, dbUserMedal, credit, medalType.getCoefficient());
                }
            });
        });
        log.info("本周定时颁布勋章任务执行完毕！下次执行将与一周后");
    }

}
