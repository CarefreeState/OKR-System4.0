package cn.lbcmmszdntnt.domain.record.service.impl;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.config.properties.StatusFlagConfig;
import cn.lbcmmszdntnt.domain.core.model.po.inner.KeyResult;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo.FirstQuadrantVO;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.FirstQuadrantService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.FourthQuadrantService;
import cn.lbcmmszdntnt.domain.record.config.CoreRecorderConfig;
import cn.lbcmmszdntnt.domain.record.model.mapper.DayRecordMapper;
import cn.lbcmmszdntnt.domain.record.model.po.CoreRecorder;
import cn.lbcmmszdntnt.domain.record.model.po.DayRecord;
import cn.lbcmmszdntnt.domain.record.model.po.RecordMap;
import cn.lbcmmszdntnt.domain.record.service.CoreRecorderService;
import cn.lbcmmszdntnt.domain.record.service.DayRecordService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

    private final CoreRecorderService coreRecorderService;

    private final FirstQuadrantService firstQuadrantService;

    private final FourthQuadrantService fourthQuadrantService;

    private final OkrCoreService okrCoreService;

    private final StatusFlagConfig statusFlagConfig;

    private final RedisLock redisLock;

    private boolean checkNeedSwitch(long gap) {
        // 相差大于一天就代表是隔天了
        return gap >= TimeUnit.DAYS.toMillis(1);
    }

    private boolean isInitialized(RecordMap recordMap) {
        return Objects.nonNull(recordMap) && Objects.nonNull(recordMap.getDayRecordId());
    }

    private int getIncrement(Boolean isCompleted, Boolean oldCompleted) {
        if(Boolean.TRUE.equals(oldCompleted)) {
            return Boolean.TRUE.equals(isCompleted) ? 0 : -1;
        }else {
            return Boolean.TRUE.equals(isCompleted) ? 1 : 0;
        }
    }

    private boolean checkOkrIsOver(Long coreId) {
        return okrCoreService.getOkrCore(coreId).getIsOver();
    }

    private void checkOverThrows(Long coreId) {
        okrCoreService.checkOverThrows(coreId);
    }

    @Override
    public DayRecord createNewRecord(Long coreId) {
        FirstQuadrantVO firstQuadrantVO = firstQuadrantService.searchFirstQuadrant(coreId);
        List<KeyResult> keyResults = firstQuadrantVO.getKeyResults();
        Integer sum = keyResults.stream()
                .parallel()
                .map(KeyResult::getProbability)
                .reduce(Integer::sum).orElse(0);
        int size = keyResults.size();
        Long quadrantId = fourthQuadrantService.searchFourthQuadrant(coreId).getId();
        DayRecord dayRecord = new DayRecord();
        dayRecord.setCoreId(coreId);
        dayRecord.setRecordDate(new Date());
        dayRecord.setCredit1(size == 0 ? Double.valueOf(0) : Double.valueOf(sum * 1.0 / size));
        dayRecord.setCredit2(0);
        dayRecord.setCredit3(0);
        dayRecord.setCredit4((int) statusFlagConfig.calculateCoreStatusFlag(quadrantId));
        Db.save(dayRecord);
        return dayRecord;
    }

    @Override
    public DayRecord switchRecord(CoreRecorder coreRecorder) {
        Long coreId = coreRecorder.getCoreId();
        DayRecord dayRecord = createNewRecord(coreId);
        Long dayRecordId = dayRecord.getId();
        // 更新一下
        String lock = CoreRecorderConfig.CORE_RECORDER_LOCK + coreId;
        redisLock.tryLockDoSomething(lock, () -> {
            RecordMap recordMap = coreRecorder.getRecordMap();
            recordMap = Objects.isNull(recordMap) ? new RecordMap() : recordMap;
            recordMap.setDayRecordId(dayRecordId);
            coreRecorder.setRecordMap(recordMap);
            coreRecorderService.lambdaUpdate()
                    .eq(CoreRecorder::getId, coreRecorder.getId())
//                    .set(CoreRecorder::getRecordMap, recordMap)
                    .update(coreRecorder);
/*
  这里不能通过 set 去指定 recordMap，因为这样会出错，只认识 coreRecorder 对象的属性，而不是 recordMap 这个 Java 对象
  那个 Json 处理器只会在，通过对象 coreRecorder 生成 sql 的时候，帮我们自动转化
  而在 lambdaUpdate 的 set 方法，是我们自己生成 sql，MP 只会在我们的这个基础上去生成 sql，所以这里的 recordMap
  我们指定的 Java 对象，MP 并没有帮我们去转化（MP 目前没有优化这一点，体谅一下）
*/
            coreRecorderService.removeCoreRecorderCache(coreId);
        }, () -> {});
        return dayRecord;
    }

    @Override
    public DayRecord getNowRecord(Long coreId) {
        CoreRecorder coreRecorder = coreRecorderService.getCoreRecorder(coreId);
        RecordMap recordMap = coreRecorder.getRecordMap();
        if(Boolean.FALSE.equals(isInitialized(recordMap))) {
            return switchRecord(coreRecorder);
        }
        Long dayRecordId = recordMap.getDayRecordId();
        DayRecord dayRecord = Db.lambdaQuery(DayRecord.class)
                .eq(DayRecord::getId, dayRecordId)
                .oneOpt()
                .orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.DAY_RECORD_NOT_EXISTS));
        long gap = System.currentTimeMillis() - dayRecord.getRecordDate().getTime();
        // 返回一个正确的 DayRecord
        return Boolean.TRUE.equals(checkNeedSwitch(gap)) ? switchRecord(coreRecorder) : dayRecord;
    }

    @Override
    public List<DayRecord> getRecords(Long coreId) {
        if(Boolean.FALSE.equals(checkOkrIsOver(coreId))) {
            getNowRecord(coreId);// 保证记录器指向的是今天的记录（如果 OKR 未结束）
        }
        return this.lambdaQuery().eq(DayRecord::getCoreId, coreId)
                .list()
                .stream()
                .sorted(Comparator.comparing(DayRecord::getId))
                .collect(Collectors.toList());
    }

    @Override
    public void recordFirstQuadrant(Long coreId) {
        checkOverThrows(coreId);
        DayRecord nowRecord = getNowRecord(coreId);
        FirstQuadrantVO firstQuadrantVO = firstQuadrantService.searchFirstQuadrant(coreId);
        List<KeyResult> keyResults = firstQuadrantVO.getKeyResults();
        Integer sum = keyResults.stream()
                .parallel()
                .map(KeyResult::getProbability)
                .reduce(Integer::sum).orElse(0);
        int size = keyResults.size();
        double credit1 = size == 0 ? Double.valueOf(0) : Double.valueOf(sum * 1.0 / size);
        this.lambdaUpdate()
                .eq(DayRecord::getId, nowRecord.getId())
                .set(DayRecord::getCredit1, credit1)
                .update();
    }

    @Override
    public void recordSecondQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        checkOverThrows(coreId);
        DayRecord nowRecord = getNowRecord(coreId);
        Integer credit2 = nowRecord.getCredit2();
        int increment = getIncrement(isCompleted, oldCompleted);
        log.info("OKR {} 第二象限积分 + {} ", coreId, increment);
        if(increment != 0) {
            this.lambdaUpdate()
                    .eq(DayRecord::getId, nowRecord.getId())
                    .set(DayRecord::getCredit2, credit2 + increment)
                    .update();
        }
    }

    @Override
    public void recordThirdQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        checkOverThrows(coreId);
        DayRecord nowRecord = getNowRecord(coreId);
        Integer credit3 = nowRecord.getCredit3();
        int increment = getIncrement(isCompleted, oldCompleted);
        log.info("OKR {} 第三象限积分 + {} ", coreId, increment);
        if(increment != 0) {
            this.lambdaUpdate()
                    .eq(DayRecord::getId, nowRecord.getId())
                    .set(DayRecord::getCredit3, credit3 + increment)
                    .update();
        }
    }

    @Override
    public void recordFourthQuadrant(Long coreId) {
        checkOverThrows(coreId);
        DayRecord nowRecord = getNowRecord(coreId);
        Long quadrantId = fourthQuadrantService.searchFourthQuadrant(coreId).getId();
        this.lambdaUpdate()
                .eq(DayRecord::getId, nowRecord.getId())
                .set(DayRecord::getCredit4, (int) statusFlagConfig.calculateCoreStatusFlag(quadrantId))
                .update();

    }
}




