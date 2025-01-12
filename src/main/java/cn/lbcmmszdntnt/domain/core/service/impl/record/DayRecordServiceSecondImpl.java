package cn.lbcmmszdntnt.domain.core.service.impl.record;


import cn.lbcmmszdntnt.domain.core.model.entity.record.DayRecord;
import cn.lbcmmszdntnt.domain.core.service.record.DayRecordCompleteService;
import cn.lbcmmszdntnt.domain.core.service.record.DayRecordService;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:46
 */
@Service
@RequiredArgsConstructor
public class DayRecordServiceSecondImpl implements DayRecordCompleteService {

    private final RedisCache redisCache;

    private final DayRecordService dayRecordService;

    @Override
    public void handle(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        String redisKey = dayRecordService.todayRedisKey(coreId);
        DayRecord dayRecord = dayRecordService.tryInitDayRecord(coreId);
        dayRecord.setCredit2(dayRecord.getCredit2() + dayRecordService.recordSecondQuadrant(coreId, isCompleted, oldCompleted));
        redisCache.setObject(redisKey, dayRecord);
    }
}
