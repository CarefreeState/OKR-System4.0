package cn.lbcmmszdntnt.domain.coredeadline.service.impl;

import cn.lbcmmszdntnt.common.util.thread.pool.IOThreadPool;
import cn.lbcmmszdntnt.domain.core.model.mapper.OkrCoreMapper;
import cn.lbcmmszdntnt.domain.coredeadline.handler.chain.DeadlineDeadlineEventHandlerChain;
import cn.lbcmmszdntnt.domain.coredeadline.handler.event.DeadlineEvent;
import cn.lbcmmszdntnt.domain.coredeadline.service.OkrCoreDeadlineService;
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
    public void checkDeadline() {
        // 获取任务
        List<DeadlineEvent> deadlineEvents = okrCoreMapper.getDeadlineEvents();
        final long nowTimestamp = System.currentTimeMillis();// 当前时间
        // 处理任务
        IOThreadPool.operateBatch(deadlineEvents, deadlineEvent -> {
            deadlineEventHandlerChain.handle(deadlineEvent, nowTimestamp);
        });
    }
}
