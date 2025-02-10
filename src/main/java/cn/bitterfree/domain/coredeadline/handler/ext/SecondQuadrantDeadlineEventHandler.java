package cn.bitterfree.domain.coredeadline.handler.ext;


import cn.bitterfree.domain.core.model.message.deadline.SecondQuadrantEvent;
import cn.bitterfree.domain.core.service.quadrant.SecondQuadrantService;
import cn.bitterfree.domain.core.util.QuadrantDeadlineMessageUtil;
import cn.bitterfree.domain.coredeadline.handler.DeadlineEventHandler;
import cn.bitterfree.domain.coredeadline.handler.event.DeadlineEvent;
import cn.bitterfree.domain.coredeadline.util.DeadlineUtil;
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
            long nextDeadTimestamp = DeadlineUtil.getNextDeadline(deadTimestamp, nowTimestamp, secondQuadrantCycle, TimeUnit.SECONDS);
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
