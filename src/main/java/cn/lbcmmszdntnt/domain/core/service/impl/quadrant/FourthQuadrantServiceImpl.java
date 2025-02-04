package cn.lbcmmszdntnt.domain.core.service.impl.quadrant;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.exception.GlobalServiceException;
import cn.lbcmmszdntnt.domain.core.model.entity.quadrant.FourthQuadrant;
import cn.lbcmmszdntnt.domain.core.model.mapper.quadrant.FourthQuadrantMapper;
import cn.lbcmmszdntnt.domain.core.model.vo.quadrant.FourthQuadrantVO;
import cn.lbcmmszdntnt.domain.core.service.quadrant.FourthQuadrantService;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
/**
* @author 马拉圈
* @description 针对表【fourth_quadrant(第四象限表)】的数据库操作Service实现
* @createDate 2024-01-20 01:04:21
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class FourthQuadrantServiceImpl extends ServiceImpl<FourthQuadrantMapper, FourthQuadrant>
    implements FourthQuadrantService {

    private final static String FOURTH_QUADRANT_CORE_MAP = "fourthQuadrantCoreMap:";

    private final static Long FOURTH_CORE_MAP_TTL = 1L;

    private final static TimeUnit FOURTH_CORE_MAP_UNIT = TimeUnit.DAYS;

    private final FourthQuadrantMapper fourthQuadrantMapper;

    private final RedisCache redisCache;

    @Override
    public FourthQuadrantVO searchFourthQuadrant(Long coreId) {
        return fourthQuadrantMapper.searchFourthQuadrant(coreId).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.FOURTH_QUADRANT_NOT_EXISTS));
    }

    @Override
    public Long getFourthQuadrantCoreId(Long id) {
        String redisKey = FOURTH_QUADRANT_CORE_MAP + id;
        return redisCache.getObject(redisKey, Long.class).orElseGet(() -> {
            // 查询
            Long coreId = this.lambdaQuery()
                    .eq(FourthQuadrant::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.FOURTH_QUADRANT_NOT_EXISTS)
                    ).getCoreId();
            redisCache.setObject(redisKey, coreId, FOURTH_CORE_MAP_TTL, FOURTH_CORE_MAP_UNIT);
            return coreId;
        });
    }
}




