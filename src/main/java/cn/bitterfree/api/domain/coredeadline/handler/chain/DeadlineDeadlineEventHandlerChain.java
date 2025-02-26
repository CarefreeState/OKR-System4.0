package cn.bitterfree.api.domain.coredeadline.handler.chain;


import cn.bitterfree.api.domain.coredeadline.handler.DeadlineEventHandler;
import cn.bitterfree.api.domain.coredeadline.handler.event.DeadlineEvent;
import cn.bitterfree.api.domain.coredeadline.handler.ext.FirstQuadrantDeadlineEventHandler;
import cn.bitterfree.api.domain.coredeadline.handler.ext.SecondQuadrantDeadlineEventHandler;
import cn.bitterfree.api.domain.coredeadline.handler.ext.ThirdQuadrantDeadlineEventHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void handle(DeadlineEvent deadlineEvent, Boolean needSend) {
        super.doNextHandler(deadlineEvent, needSend);
        log.warn("责任链处理完毕！");
    }
}
