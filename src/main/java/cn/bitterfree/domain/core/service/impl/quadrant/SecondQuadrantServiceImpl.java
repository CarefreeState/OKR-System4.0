package cn.bitterfree.domain.core.service.impl.quadrant;


import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.domain.core.constants.OkrCoreConstants;
import cn.bitterfree.domain.core.model.dto.quadrant.InitQuadrantDTO;
import cn.bitterfree.domain.core.model.entity.OkrCore;
import cn.bitterfree.domain.core.model.entity.quadrant.SecondQuadrant;
import cn.bitterfree.domain.core.model.mapper.quadrant.SecondQuadrantMapper;
import cn.bitterfree.domain.core.model.message.deadline.SecondQuadrantEvent;
import cn.bitterfree.domain.core.model.vo.quadrant.SecondQuadrantVO;
import cn.bitterfree.domain.core.service.quadrant.SecondQuadrantService;
import cn.bitterfree.domain.core.util.QuadrantDeadlineMessageUtil;
import cn.bitterfree.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
/**
* @author 马拉圈
* @description 针对表【second_quadrant(第二象限表)】的数据库操作Service实现
* @createDate 2024-01-20 01:04:21
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class SecondQuadrantServiceImpl extends ServiceImpl<SecondQuadrantMapper, SecondQuadrant>
    implements SecondQuadrantService {

    private final static String SECOND_QUADRANT_CORE_MAP = "secondQuadrantCoreMap:";

    private final static Long SECOND_CORE_MAP_TTL = 1L;

    private final static TimeUnit SECOND_CORE_MAP_UNIT = TimeUnit.DAYS;

    private final SecondQuadrantMapper secondQuadrantMapper;

    private final RedisCache redisCache;

    @Override
    @Transactional
    public void initSecondQuadrant(InitQuadrantDTO initQuadrantDTO) {
        Long id = initQuadrantDTO.getId();
        // 查询是否初始化过
        Date deadline = this.lambdaQuery()
                .eq(SecondQuadrant::getId, id)
                .one()
                .getDeadline();
        if(Objects.nonNull(deadline)) {
            throw new GlobalServiceException("第二象限无法再次初始化！",
                    GlobalServiceStatusCode.SECOND_QUADRANT_UPDATE_ERROR);
        }
        Integer quadrantCycle = initQuadrantDTO.getQuadrantCycle();
        deadline = initQuadrantDTO.getDeadline();
        // 查询内核 ID
        Long coreId = this.lambdaQuery()
                .eq(SecondQuadrant::getId, id)
                .one()
                .getCoreId();
        Boolean isOver = Db.lambdaQuery(OkrCore.class)
                .eq(OkrCore::getId, coreId)
                .one()
                .getIsOver();
        if(Boolean.TRUE.equals(isOver)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_OVER);
        }
        // 为 core 设置周期
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(coreId);
        updateOkrCore.setSecondQuadrantCycle(quadrantCycle);
        Db.lambdaUpdate(OkrCore.class).eq(OkrCore::getId, coreId).update(updateOkrCore);
        // 设置象限的截止时间
        SecondQuadrant updateQuadrant = new SecondQuadrant();
        updateQuadrant.setId(id);
        updateQuadrant.setDeadline(deadline);
        this.lambdaUpdate().eq(SecondQuadrant::getId, id).update(updateQuadrant);
        // 发起一个定时任务
        SecondQuadrantEvent event = SecondQuadrantEvent.builder()
                .coreId(coreId).id(id).cycle(quadrantCycle).deadline(deadline).build();
        QuadrantDeadlineMessageUtil.scheduledUpdateSecondQuadrant(event);
        // 清楚缓存
        redisCache.deleteObject(OkrCoreConstants.OKR_CORE_ID_MAP + coreId);
    }

    @Override
    public void updateDeadline(Long id, Date date) {
        SecondQuadrant updateQuadrant = new SecondQuadrant();
        updateQuadrant.setId(id);
        updateQuadrant.setDeadline(date);
        this.updateById(updateQuadrant);
    }

    @Override
    public SecondQuadrantVO searchSecondQuadrant(Long coreId) {
        return secondQuadrantMapper.searchSecondQuadrant(coreId).orElseThrow(() ->
            new GlobalServiceException(GlobalServiceStatusCode.SECOND_QUADRANT_NOT_EXISTS)
        );
    }

    @Override
    public Long getSecondQuadrantCoreId(Long id) {
        String redisKey = SECOND_QUADRANT_CORE_MAP + id;
        return redisCache.getObject(redisKey, Long.class).orElseGet(() -> {
            // 查询
            Long coreId = this.lambdaQuery()
                    .eq(SecondQuadrant::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.SECOND_QUADRANT_NOT_EXISTS)
                    ).getCoreId();
            redisCache.setObject(redisKey, coreId, SECOND_CORE_MAP_TTL, SECOND_CORE_MAP_UNIT);
            return coreId;
        });
    }
}




