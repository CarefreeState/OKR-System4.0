package cn.lbcmmszdntnt.domain.coredeadline.handler.ext;


import cn.lbcmmszdntnt.domain.core.model.message.deadline.ThirdQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.service.quadrant.ThirdQuadrantService;
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
public class ThirdQuadrantDeadlineEventHandler extends DeadlineEventHandler {

    private final ThirdQuadrantService thirdQuadrantService;

    @Override
    public void handle(DeadlineEvent deadlineEvent, long nowTimestamp, Boolean needSend) {
        ThirdQuadrantEvent thirdQuadrantEvent = deadlineEvent.getThirdQuadrantEvent();
        Long id = thirdQuadrantEvent.getCoreId();
        Long thirdQuadrantId = thirdQuadrantEvent.getId();
        Date thirdQuadrantDeadline = thirdQuadrantEvent.getDeadline();
        Integer thirdQuadrantCycle = thirdQuadrantEvent.getCycle();
        // 是否设置了第三象限截止时间和周期
        if(Objects.nonNull(thirdQuadrantDeadline) && Objects.nonNull(thirdQuadrantCycle)) {
            // 获取一个正确的截止点
            long deadTimestamp = thirdQuadrantDeadline.getTime();
            long nextDeadTimestamp = deadTimestamp;
            final long cycle = TimeUnit.SECONDS.toMillis(thirdQuadrantCycle);
            while(nextDeadTimestamp <= nowTimestamp) {
                nextDeadTimestamp += cycle;
            }
            Date nextDeadline = new Date(nextDeadTimestamp);
            Boolean flag = nextDeadTimestamp != deadTimestamp;
            // 更新截止时间
            if(Boolean.TRUE.equals(flag)) {
                log.info("处理事件：内核 ID {}，第三象限 ID {}，第三象限截止时间 {}，第三象限周期 {}",
                        id, thirdQuadrantId, thirdQuadrantDeadline, thirdQuadrantCycle);
                thirdQuadrantService.updateDeadline(thirdQuadrantId, nextDeadline);
            }
            if(flag || needSend) {
                // 发起定时任务
                thirdQuadrantEvent.setDeadline(nextDeadline);
                QuadrantDeadlineMessageUtil.scheduledUpdateThirdQuadrant(thirdQuadrantEvent);
            }
        }
        super.doNextHandler(deadlineEvent, nowTimestamp, needSend);//执行下一个责任处理器
    }
}
