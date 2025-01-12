package cn.lbcmmszdntnt.domain.core.handler.ext;


import cn.lbcmmszdntnt.domain.core.handler.DeadlineEventHandler;
import cn.lbcmmszdntnt.domain.core.handler.event.DeadlineEvent;
import cn.lbcmmszdntnt.domain.core.model.event.deadline.FirstQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-12
 * Time: 9:49
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FirstQuadrantDeadlineEventHandler extends DeadlineEventHandler {

    private final OkrCoreService okrCoreService;

    @Override
    public void handle(DeadlineEvent deadlineEvent, long nowTimestamp) {
        FirstQuadrantEvent firstQuadrantEvent = deadlineEvent.getFirstQuadrantEvent();
        Long id = firstQuadrantEvent.getCoreId();
        Date firstQuadrantDeadline = firstQuadrantEvent.getDeadline();
        // 判断是否截止
        if(Objects.nonNull(firstQuadrantDeadline) &&
                firstQuadrantDeadline.getTime() <= nowTimestamp) {
            log.info("处理事件：内核 ID {}，第一象限截止时间 {}", id, firstQuadrantDeadline);
            okrCoreService.complete(id);
            return; // 责任链终止
        }
        super.doNextHandler(deadlineEvent, nowTimestamp);//执行下一个责任处理器
    }
}
