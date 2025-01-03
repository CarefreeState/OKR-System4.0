package cn.lbcmmszdntnt.domain.record.service.impl;


import cn.lbcmmszdntnt.domain.record.model.mapper.CoreRecorderMapper;
import cn.lbcmmszdntnt.domain.record.model.po.CoreRecorder;
import cn.lbcmmszdntnt.domain.record.model.po.RecordMap;
import cn.lbcmmszdntnt.domain.record.service.CoreRecorderService;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【core_recorder(OKR 内核记录器表)】的数据库操作Service实现
* @createDate 2024-04-21 02:02:14
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class CoreRecorderServiceImpl extends ServiceImpl<CoreRecorderMapper, CoreRecorder>
    implements CoreRecorderService {

    private final static String CORE_RECORDER_MAP = "coreRecorderMap:";

    private final static Long CORE_RECORD_MAP_TTL = 1L;

    private final static TimeUnit CORE_RECORDER_MAP_UNIT = TimeUnit.DAYS;

    private final RedisCache redisCache;

    @Override
    public void initRecordMap(CoreRecorder coreRecorder, Long coreId) {
        coreRecorder.setCoreId(coreId);
        coreRecorder.setRecordMap(new RecordMap());
    }

    @Override
    public CoreRecorder initCoreRecorder(Long coreId) {
        CoreRecorder coreRecorder = new CoreRecorder();
        initRecordMap(coreRecorder, coreId);
        this.save(coreRecorder);
        return coreRecorder;
    }

    @Override
    public CoreRecorder getCoreRecorder(Long coreId) {
        String redisKey = CORE_RECORDER_MAP + coreId;
        return redisCache.getObject(redisKey, CoreRecorder.class).orElseGet(() -> {
            CoreRecorder coreRecorder = this.lambdaQuery().eq(CoreRecorder::getCoreId, coreId)
                    .oneOpt().orElseGet(() -> initCoreRecorder(coreId));
            redisCache.setObject(redisKey, coreRecorder, CORE_RECORD_MAP_TTL, CORE_RECORDER_MAP_UNIT);
            return coreRecorder;
        });
    }

    @Override
    public void removeCoreRecorderCache(Long coreId) {
        redisCache.deleteObject(CORE_RECORDER_MAP + coreId);
    }
}




