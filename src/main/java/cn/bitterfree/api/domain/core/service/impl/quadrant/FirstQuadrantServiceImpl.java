package cn.bitterfree.api.domain.core.service.impl.quadrant;


import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.model.entity.quadrant.FirstQuadrant;
import cn.bitterfree.api.domain.core.model.mapper.quadrant.FirstQuadrantMapper;
import cn.bitterfree.api.domain.core.model.vo.quadrant.FirstQuadrantVO;
import cn.bitterfree.api.domain.core.service.quadrant.FirstQuadrantService;
import cn.bitterfree.api.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
/**
* @author 马拉圈
* @description 针对表【first_quadrant(第一象限表)】的数据库操作Service实现
* @createDate 2024-01-20 01:04:21
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class FirstQuadrantServiceImpl extends ServiceImpl<FirstQuadrantMapper, FirstQuadrant>
    implements FirstQuadrantService {

    private final static String FIRST_QUADRANT_CORE_MAP = "firstQuadrantCoreMap:";

    private final static Long FIRST_CORE_MAP_TTL = 1L;

    private final static TimeUnit FIRST_CORE_MAP_UNIT = TimeUnit.DAYS;

    private final FirstQuadrantMapper firstQuadrantMapper;

    private final RedisCache redisCache;

    @Override
    @Transactional
    public void initFirstQuadrant(FirstQuadrant firstQuadrant) {
        Long id = firstQuadrant.getId();
        // 查询是否是第一次修改
        FirstQuadrant quadrant = this.lambdaQuery()
                .eq(FirstQuadrant::getId, id)
                .one();
        if(StringUtils.hasText(quadrant.getObjective()) || Objects.nonNull(quadrant.getDeadline())) {
            throw new GlobalServiceException("第一象限无法再次初始化！",
                    GlobalServiceStatusCode.FIRST_QUADRANT_UPDATE_ERROR);
        }
        // 构造对象
        FirstQuadrant updateQuadrant = new FirstQuadrant();
        updateQuadrant.setId(id);
        updateQuadrant.setDeadline(firstQuadrant.getDeadline());
        updateQuadrant.setObjective(firstQuadrant.getObjective());
        // 更新
        this.updateById(updateQuadrant);
    }

    @Override
    public FirstQuadrantVO searchFirstQuadrant(Long coreId) {
        return firstQuadrantMapper.searchFirstQuadrant(coreId).orElseThrow(() ->
                new GlobalServiceException("内核 ID: " + coreId, GlobalServiceStatusCode.FIRST_QUADRANT_NOT_EXISTS)
        );
    }

    @Override
    public Long getFirstQuadrantCoreId(Long id) {
        String redisKey = FIRST_QUADRANT_CORE_MAP + id;
        return redisCache.getObject(redisKey, Long.class).orElseGet(() -> {
            // 查询
            Long coreId = this.lambdaQuery()
                    .eq(FirstQuadrant::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.FIRST_QUADRANT_NOT_EXISTS)
                    ).getCoreId();
            redisCache.setObject(redisKey, coreId, FIRST_CORE_MAP_TTL, FIRST_CORE_MAP_UNIT);
            return coreId;
        });
    }
}




