package cn.bitterfree.domain.record.service.impl;


import cn.bitterfree.domain.record.model.entity.DayRecord;
import cn.bitterfree.domain.record.service.DayRecordCompleteService;
import cn.bitterfree.domain.record.service.DayRecordService;
import cn.bitterfree.redis.cache.RedisCache;
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
public class DayRecordServiceThirdImpl implements DayRecordCompleteService {

    private final RedisCache redisCache;

    private final DayRecordService dayRecordService;

    @Override
    public void handle(DayRecord dayRecord, Boolean isCompleted, Boolean oldCompleted) {
        dayRecord.setCredit3(dayRecord.getCredit3() +
                dayRecordService.recordThirdQuadrant(dayRecord.getCoreId(), isCompleted, oldCompleted));
    }
}
