package cn.bitterfree.api.domain.core.service.impl.inner;


import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.model.entity.inner.KeyResult;
import cn.bitterfree.api.domain.core.model.mapper.inner.KeyResultMapper;
import cn.bitterfree.api.domain.core.service.inner.KeyResultService;
import cn.bitterfree.api.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【key_result(关键结果表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class KeyResultServiceImpl extends ServiceImpl<KeyResultMapper, KeyResult>
    implements KeyResultService {

    private final static String KR_FIRST_QUADRANT_MAP = "krFirstQuadrantMap:";

    private final static Long KR_FIRST_QUADRANT_TTL = 6L;

    private final static TimeUnit KR_FIRST_QUADRANT_UNIT = TimeUnit.HOURS;

    private final RedisCache redisCache;


    @Override
    public Long addResultService(KeyResult keyResult) {
        // 1. 提取需要的数据
        KeyResult newKeyResult = new KeyResult();
        newKeyResult.setFirstQuadrantId(keyResult.getFirstQuadrantId());
        newKeyResult.setContent(keyResult.getContent());
        newKeyResult.setProbability(keyResult.getProbability());
        // 2. 插入
        this.save(newKeyResult);
        Long id = newKeyResult.getId();
        log.info("新增关键结果： key result id : {}", id);
        return id;
    }

    @Override
    public KeyResult updateProbability(KeyResult keyResult) {
        Long id = keyResult.getId();
        KeyResult oldKeyResult = lambdaQuery().eq(KeyResult::getId, id).oneOpt().orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.KEY_RESULT_NOT_EXISTS));
        // 1. 提取需要更新的数据
        KeyResult updateKeyResult = new KeyResult();
        updateKeyResult.setId(id);
        updateKeyResult.setProbability(keyResult.getProbability());
        // 2. 更新
        this.updateById(updateKeyResult);
        return oldKeyResult;
    }

    @Override
    public Long getFirstQuadrantId(Long id) {
        String redisKey = KR_FIRST_QUADRANT_MAP + id;
        return redisCache.getObject(redisKey, Long.class).orElseGet(() -> {
            // 查询数据库
            Long firstQuadrantId = this.lambdaQuery()
                    .eq(KeyResult::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.KEY_RESULT_NOT_EXISTS)
                    ).getFirstQuadrantId();
            redisCache.setObject(redisKey, firstQuadrantId, KR_FIRST_QUADRANT_TTL, KR_FIRST_QUADRANT_UNIT);
            return firstQuadrantId;
        });
    }

}




