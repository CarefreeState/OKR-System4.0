package cn.lbcmmszdntnt.domain.coredeadline.config;

import cn.lbcmmszdntnt.domain.coredeadline.service.OkrCoreDeadlineService;
import cn.lbcmmszdntnt.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-13
 * Time: 12:20
 */
@Configuration
@RequiredArgsConstructor
public class OkrCoreDeadlineXxlJobConfig {

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static int TRIGGER_STATUS = 0; // 不启动

    private final static String CRON = "59 59 23 ? * 1 *"; // 每周日结束前一刻

    private final OkrCoreDeadlineService okrCoreDeadlineService;

    @XxlJob(value = "checkDeadline")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR,  triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】检查 OKR 截止时间")
    public void checkDeadline() {
        okrCoreDeadlineService.checkDeadline();
    }

}
