package cn.bitterfree.domain.record.service;


import cn.bitterfree.domain.record.model.entity.DayRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
* @author 马拉圈
* @description 针对表【day_record(OKR 内核日记录表)】的数据库操作Service
* @createDate 2024-04-21 02:02:14
*/
public interface DayRecordService extends IService<DayRecord> {

    String dateRedisKeyPrefix(Date date);
    String yesterdayRedisKeyPrefix();
    String todayRedisKeyPrefix();
    String todayRedisKey(Long coreId);

    DayRecord tryInitDayRecord(Long coreId);

    List<DayRecord> getDayRecords(Long coreId);

    Double recordFirstQuadrant(Long coreId);

    Integer recordSecondQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted);

    Integer recordThirdQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted);

    Integer recordFourthQuadrant(Long coreId);

}
