package cn.lbcmmszdntnt.domain.coredeadline.handler.ext;


import cn.lbcmmszdntnt.domain.core.model.message.deadline.SecondQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.service.quadrant.SecondQuadrantService;
import cn.lbcmmszdntnt.domain.core.util.QuadrantDeadlineMessageUtil;
import cn.lbcmmszdntnt.domain.coredeadline.handler.DeadlineEventHandler;
import cn.lbcmmszdntnt.domain.coredeadline.handler.event.DeadlineEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-12
 * Time: 9:50
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecondQuadrantDeadlineEventHandler extends DeadlineEventHandler {

    private final SecondQuadrantService secondQuadrantService;

    @Override
    public void handle(DeadlineEvent deadlineEvent, long nowTimestamp, Boolean needSend) {
        SecondQuadrantEvent secondQuadrantEvent = deadlineEvent.getSecondQuadrantEvent();
        Long id = secondQuadrantEvent.getCoreId();
        Long secondQuadrantId = secondQuadrantEvent.getId();
        Date secondQuadrantDeadline = secondQuadrantEvent.getDeadline();
        Integer secondQuadrantCycle = secondQuadrantEvent.getCycle();
        // 是否设置了第二象限截止时间和周期
        if(Objects.nonNull(secondQuadrantDeadline) && Objects.nonNull(secondQuadrantCycle)) {
            // 获取一个正确的截止点
            long deadTimestamp = secondQuadrantDeadline.getTime();
            long nextDeadTimestamp = deadTimestamp;
            final long cycle = TimeUnit.SECONDS.toMillis(secondQuadrantCycle);
            while(nextDeadTimestamp <= nowTimestamp) {
                nextDeadTimestamp += cycle;
            }
            Date nextDeadline = new Date(nextDeadTimestamp);
            Boolean flag = nextDeadTimestamp != deadTimestamp;
            // 更新截止时间
            if(Boolean.TRUE.equals(flag)) {
                log.info("处理事件：内核 ID {}，第二象限 ID {}，第二象限截止时间 {}，第二象限周期 {}",
                        id, secondQuadrantId, secondQuadrantDeadline, secondQuadrantCycle);
                secondQuadrantService.updateDeadline(secondQuadrantId, nextDeadline);
            }
            if(flag || needSend) {
                // 发起定时任务
                secondQuadrantEvent.setDeadline(nextDeadline);
                QuadrantDeadlineMessageUtil.scheduledUpdateSecondQuadrant(secondQuadrantEvent);
            }
        }
        super.doNextHandler(deadlineEvent, nowTimestamp, needSend);//执行下一个责任处理器
    }
}
