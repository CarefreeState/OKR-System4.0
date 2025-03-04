package cn.bitterfree.api.domain.core.service.impl.inner;


import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.model.entity.inner.Action;
import cn.bitterfree.api.domain.core.model.mapper.inner.ActionMapper;
import cn.bitterfree.api.domain.core.service.inner.ActionService;
import cn.bitterfree.api.domain.core.service.inner.TaskService;
import cn.bitterfree.api.domain.core.service.quadrant.ThirdQuadrantService;
import cn.bitterfree.api.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【action(行动表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action>
    implements ActionService, TaskService {

    private final static String ACTION_QUADRANT_MAP = "actionQuadrantMap:";

    private final static Long ACTION_QUADRANT_TTL = 6L;

    private final static TimeUnit ACTION_QUADRANT_UNIT = TimeUnit.HOURS;

    private final ActionMapper actionMapper;

    private final RedisCache redisCache;

    private final ThirdQuadrantService thirdQuadrantService;

    @Override
    public Long addTask(Long quadrantId, String content) {
        // 构造对象
        Action action = new Action();
        action.setContent(content);
        action.setThirdQuadrantId(quadrantId);
        // 插入
        actionMapper.insert(action);
        Long id = action.getId();
        log.info("为第三象限 {} 插入一条行动 {} -- {}", quadrantId, id, content);
        return id;
    }

    @Override
    public void removeTask(Long id) {
        // 删除
        boolean ret = Db.lambdaUpdate(Action.class)
                .eq(Action::getId, id)
                .remove();
        if(Boolean.TRUE.equals(ret)) {
            log.info("成功为第三象限删除一条行动 {}", id);
        }
    }

    @Override
    public Boolean updateTask(Long id, String content, Boolean isCompleted) {
        Boolean oldCompleted = Optional.ofNullable(actionMapper.selectById(id)).orElseGet(Action::new).getIsCompleted();
        Action updateAction = new Action();
        updateAction.setId(id);
        updateAction.setContent(content);
        updateAction.setIsCompleted(isCompleted);
        actionMapper.updateById(updateAction);
        log.info("成功更新一条行动 {} {} {}", id, content, isCompleted);
        return oldCompleted;
    }

    @Override
    public Long getTaskQuadrantId(Long id) {
        String redisKey = ACTION_QUADRANT_MAP + id;
        return redisCache.getObject(redisKey, Long.class).orElseGet(() -> {
            // 查询数据库
            Long thirdQuadrantId = this.lambdaQuery()
                    .eq(Action::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.TASK_NOT_EXISTS)
                    ).getThirdQuadrantId();
            redisCache.setObject(redisKey, thirdQuadrantId, ACTION_QUADRANT_TTL, ACTION_QUADRANT_UNIT);
            return thirdQuadrantId;
        });
    }

    @Override
    public Long getTaskCoreId(Long quadrantId) {
        return thirdQuadrantService.getThirdQuadrantCoreId(quadrantId);
    }

}




