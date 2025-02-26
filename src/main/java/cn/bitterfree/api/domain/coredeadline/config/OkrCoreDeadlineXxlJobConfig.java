package cn.bitterfree.api.domain.coredeadline.config;

import cn.bitterfree.api.domain.coredeadline.service.OkrCoreDeadlineService;
import cn.bitterfree.api.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-13
 * Time: 12:20
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class OkrCoreDeadlineXxlJobConfig {

    private final static String ROUTE = "ROUND";
    private final static int TRIGGER_STATUS = 0; // 不启动
    private final static String CRON = "59 59 23 ? * 1 *"; // 每周日结束前一刻

    private final OkrCoreDeadlineService okrCoreDeadlineService;

    @XxlJob(value = "checkDeadline")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE, triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】每周一次的检查所有 OKR 四象限截止时间（只有更新时才发生延时消息）")
    @Transactional
    public void checkDeadline() {
        log.warn("--> --> --> --> 开始检查 OKR 截止时间 --> --> --> -->");
        okrCoreDeadlineService.checkDeadline(Boolean.FALSE);
        log.warn("<-- <-- <-- <-- <-- 检查完毕成功 <-- <-- <-- <-- <--");
    }

    @XxlJob(value = "recoverDeadline")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE, triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】每周一次的检查所有 OKR 四象限截止时间（无论如何都发送延时消息）")
    @Transactional
    public void recoverDeadline() {
        log.warn("--> --> --> --> 开始恢复 OKR 周期任务 --> --> --> -->");
        okrCoreDeadlineService.checkDeadline(Boolean.TRUE);
        log.warn("<-- <-- <-- <-- <-- 检查完毕成功 <-- <-- <-- <-- <--");
    }

}
