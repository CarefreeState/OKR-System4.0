package cn.lbcmmszdntnt.domain.medal.init;


import cn.lbcmmszdntnt.common.util.thread.pool.IOThreadPool;
import cn.lbcmmszdntnt.domain.core.config.properties.StatusFlagConfig;
import cn.lbcmmszdntnt.domain.medal.enums.MedalType;
import cn.lbcmmszdntnt.domain.medal.model.entity.UserMedal;
import cn.lbcmmszdntnt.domain.medal.service.UserMedalService;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.xxljob.annotation.XxlRegister;
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

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static int TRIGGER_STATUS = 1;

    private final static String CRON = "59 59 23 * * 0";

    private final StatusFlagConfig statusFlagConfig;

    private final UserService userService;

    private final UserMedalService userMedalService;

    // 渐入佳境勋章
    @XxlJob(value = "issueGreatStateMedal")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR,  triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】每周一次的勋章检查")
    public void issueGreatStateMedal() {
        List<Long> userIds = userService.lambdaQuery()
                .select(User::getId)
                .list()
                .stream()
                .map(User::getId)
                .toList();
        // 数据量很大，需要分批处理
        IOThreadPool.operateBatch(userIds, userId -> {
            MedalType medalType = MedalType.GREAT_STATE;
            Long medalId = medalType.getMedalId();
            // 查看用户当前未完成的个人 OKR 的所有状态指标，算加权平均值
            double average = statusFlagConfig.calculateStatusFlag(userId);
            // 判断是否计数
            log.info("用户 {} 状态指标评估： {}", userId, average);
            if (statusFlagConfig.isTouch(average)) {
                UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
                long credit = Objects.isNull(dbUserMedal) ? 1 : dbUserMedal.getCredit() + 1;
                userMedalService.saveUserMedal(userId, medalId, dbUserMedal, credit, medalType.getCoefficient());
            }
        });
        log.info("本周定时颁布勋章任务执行完毕！下次执行将与一周后");
    }

}
