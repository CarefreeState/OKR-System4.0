package cn.bitterfree.api.domain.record.service.impl;


import cn.bitterfree.api.common.util.convert.DateTimeUtil;
import cn.bitterfree.api.domain.core.config.StatusFlagConfig;
import cn.bitterfree.api.domain.core.model.entity.inner.KeyResult;
import cn.bitterfree.api.domain.core.model.vo.quadrant.FirstQuadrantVO;
import cn.bitterfree.api.domain.core.service.quadrant.FirstQuadrantService;
import cn.bitterfree.api.domain.core.service.quadrant.FourthQuadrantService;
import cn.bitterfree.api.domain.record.constants.DayRecordConstants;
import cn.bitterfree.api.domain.record.model.entity.DayRecord;
import cn.bitterfree.api.domain.record.model.mapper.DayRecordMapper;
import cn.bitterfree.api.domain.record.service.DayRecordService;
import cn.bitterfree.api.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 马拉圈
* @description 针对表【day_record(OKR 内核日记录表)】的数据库操作Service实现
* @createDate 2024-04-21 02:02:14
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class DayRecordServiceImpl extends ServiceImpl<DayRecordMapper, DayRecord>
    implements DayRecordService {

    private final RedisCache redisCache;

    private final FirstQuadrantService firstQuadrantService;

    private final FourthQuadrantService fourthQuadrantService;

    private final StatusFlagConfig statusFlagConfig;

    @Override
    public String dateRedisKeyPrefix(Date date) {
        return String.format(DayRecordConstants.DAY_RECORD_DATE_CACHE_PREFIX, DateTimeUtil.getOnlyDateFormat(date));
    }

    @Override
    public String yesterdayRedisKeyPrefix() {
        return dateRedisKeyPrefix((new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1))));
    }

    @Override
    public String todayRedisKeyPrefix() {
        return dateRedisKeyPrefix((new Date()));
    }

    @Override
    public String todayRedisKey(Long coreId) {
        return todayRedisKeyPrefix() + coreId;
    }

    @Override
    @Transactional
    public DayRecord tryInitDayRecord(Long coreId) {
        String redisKey = todayRedisKey(coreId);
        Date now = new Date();
        DayRecord dayRecord = redisCache.getObject(redisKey, DayRecord.class).orElseGet(() -> {
            DayRecord defaultDayRecord = new DayRecord() {{
                this.setCoreId(coreId);
                this.setRecordDate(DateTimeUtil.beginOfDay(new Date()));
                this.setCredit1(recordFirstQuadrant(coreId));
                this.setCredit2(0);
                this.setCredit3(0);
                this.setCredit4(recordFourthQuadrant(coreId));
                this.setCreateTime(now);
            }};
            redisCache.setObject(redisKey, defaultDayRecord, DayRecordConstants.DAY_RECORD_DATE_CACHE_TIMEOUT, DayRecordConstants.DAY_RECORD_DATE_CACHE_TIMEUNIT);
            return defaultDayRecord;
        });
        dayRecord.setUpdateTime(now);
        return dayRecord;
    }

    @Override
    @Transactional
    public List<DayRecord> getDayRecords(Long coreId) {
        List<DayRecord> recordList = this.lambdaQuery().eq(DayRecord::getCoreId, coreId).list();
        recordList.add(tryInitDayRecord(coreId));
        return recordList.stream().sorted(Comparator.comparing(DayRecord::getRecordDate)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Double recordFirstQuadrant(Long coreId) {
        FirstQuadrantVO firstQuadrantVO = firstQuadrantService.searchFirstQuadrant(coreId);
        List<KeyResult> keyResults = firstQuadrantVO.getKeyResults();
        Integer sum = keyResults.stream()
                .parallel()
                .map(KeyResult::getProbability)
                .reduce(Integer::sum).orElse(0);
        int size = keyResults.size();
        return size == 0 ? 0.0 : sum * 1.0 / size;
    }

    @Override
    @Transactional
    public Integer recordSecondQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        int increment = Boolean.TRUE.equals(oldCompleted) ? (Boolean.TRUE.equals(isCompleted) ? 0 : -1) : (Boolean.TRUE.equals(isCompleted) ? 1 : 0);
        log.info("OKR {} 第二象限积分 + {} ", coreId, increment);
        return increment;
    }

    @Override
    @Transactional
    public Integer recordThirdQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        int increment = Boolean.TRUE.equals(oldCompleted) ? (Boolean.TRUE.equals(isCompleted) ? 0 : -1) : (Boolean.TRUE.equals(isCompleted) ? 1 : 0);
        log.info("OKR {} 第三象限积分 + {} ", coreId, increment);
        return increment;
    }

    @Override
    @Transactional
    public Integer recordFourthQuadrant(Long coreId) {
        Long quadrantId = fourthQuadrantService.searchFourthQuadrant(coreId).getId();
        return (int) statusFlagConfig.calculateCoreStatusFlag(quadrantId);
    }
}




