package cn.lbcmmszdntnt.domain.coredeadline.handler.chain;


import cn.lbcmmszdntnt.domain.coredeadline.handler.DeadlineEventHandler;
import cn.lbcmmszdntnt.domain.coredeadline.handler.event.DeadlineEvent;
import cn.lbcmmszdntnt.domain.coredeadline.handler.ext.FirstQuadrantDeadlineEventHandler;
import cn.lbcmmszdntnt.domain.coredeadline.handler.ext.SecondQuadrantDeadlineEventHandler;
import cn.lbcmmszdntnt.domain.coredeadline.handler.ext.ThirdQuadrantDeadlineEventHandler;
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

    @PostConstruct
    public void doPostConstruct() {
        firstQuadrantEventHandler.setNextHandler(secondQuadrantEventHandler);
        secondQuadrantEventHandler.setNextHandler(thirdQuadrantEventHandler);
        this.setNextHandler(firstQuadrantEventHandler);
    }

    @Override
    public void handle(DeadlineEvent deadlineEvent, long nowTimestamp) {
        super.doNextHandler(deadlineEvent, nowTimestamp);
        log.warn("责任链处理完毕！");
    }
}
