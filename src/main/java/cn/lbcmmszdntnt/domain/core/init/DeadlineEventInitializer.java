package cn.lbcmmszdntnt.domain.core.init;


import cn.lbcmmszdntnt.common.util.thread.pool.IOThreadPool;
import cn.lbcmmszdntnt.domain.core.deadline.chain.DeadlineDeadlineEventHandlerChain;
import cn.lbcmmszdntnt.domain.core.model.entity.event.DeadlineEvent;
import cn.lbcmmszdntnt.domain.core.model.mapper.OkrCoreMapper;
import cn.lbcmmszdntnt.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeadlineEventInitializer implements ApplicationListener<ApplicationStartingEvent> {

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static int TRIGGER_STATUS = 0;

    private final static String CRON = "59 59 23 ? * 1 *";

    private final OkrCoreMapper okrCoreMapper;

    private final DeadlineDeadlineEventHandlerChain deadlineEventHandlerChain;

    private void handleEvent(DeadlineEvent deadlineEvent) {
        final long nowTimestamp = System.currentTimeMillis();// 当前时间
        deadlineEventHandlerChain.handle(deadlineEvent, nowTimestamp);
    }

    private void action() {
        // 获取定时任务
        List<DeadlineEvent> deadlineEvents = okrCoreMapper.getDeadlineEvents();
        // 处理定时任务（若未过期任务，则重新计算时间并发送延时任务）
        IOThreadPool.operateBatch(deadlineEvents, this::handleEvent);
    }

    @XxlJob(value = "initDeadlineJob")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR,  triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】刷新截止时间")
    public void initDeadlineJob() {
        action();
    }

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        log.warn("--> --> --> 应用启动成功 --> 开始恢复定时任务 --> --> -->");
        action();
        log.warn("<-- <-- <-- <-- <-- 定时任务恢复成功 <-- <-- <-- <-- <--");
    }
}
