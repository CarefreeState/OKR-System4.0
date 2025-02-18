package cn.bitterfree.api.domain.coredeadline.service.impl;

import cn.bitterfree.api.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.api.domain.core.model.mapper.OkrCoreMapper;
import cn.bitterfree.api.domain.coredeadline.handler.chain.DeadlineDeadlineEventHandlerChain;
import cn.bitterfree.api.domain.coredeadline.handler.event.DeadlineEvent;
import cn.bitterfree.api.domain.coredeadline.service.OkrCoreDeadlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-13
 * Time: 12:18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OkrCoreDeadlineServiceImpl implements OkrCoreDeadlineService {

    private final OkrCoreMapper okrCoreMapper;

    private final DeadlineDeadlineEventHandlerChain deadlineEventHandlerChain;

    @Override
    public void checkDeadline(Boolean needSend) {
        // 获取任务
        List<DeadlineEvent> deadlineEvents = okrCoreMapper.getDeadlineEvents();
        // 处理任务
        IOThreadPool.operateBatch(deadlineEvents, events -> {
            events.forEach(event -> {
                deadlineEventHandlerChain.handle(event, needSend);
            });
        });
    }
}
