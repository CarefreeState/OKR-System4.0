package cn.lbcmmszdntnt.domain.core.deadline.chain;


import cn.lbcmmszdntnt.domain.core.deadline.DeadlineEventHandler;
import cn.lbcmmszdntnt.domain.core.deadline.ext.FirstQuadrantDeadlineEventHandler;
import cn.lbcmmszdntnt.domain.core.deadline.ext.SecondQuadrantDeadlineEventHandler;
import cn.lbcmmszdntnt.domain.core.deadline.ext.ThirdQuadrantDeadlineEventHandler;
import cn.lbcmmszdntnt.domain.core.model.entity.event.DeadlineEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-22
 * Time: 19:49
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DeadlineDeadlineEventHandlerChain extends DeadlineEventHandler {

    private final FirstQuadrantDeadlineEventHandler firstQuadrantEventHandler;

    private final SecondQuadrantDeadlineEventHandler secondQuadrantEventHandler;

    private final ThirdQuadrantDeadlineEventHandler thirdQuadrantEventHandler;

    private DeadlineEventHandler initHandlerChain() {
        firstQuadrantEventHandler.setNextHandler(secondQuadrantEventHandler);
        secondQuadrantEventHandler.setNextHandler(thirdQuadrantEventHandler);
        return firstQuadrantEventHandler;
    }

    @PostConstruct
    public void doPostConstruct() {
        this.setNextHandler(initHandlerChain());
    }
    @Override
    public void handle(DeadlineEvent deadlineEvent, long nowTimestamp) {
        super.doNextHandler(deadlineEvent, nowTimestamp);
        log.warn("责任链处理完毕！");
    }
}
