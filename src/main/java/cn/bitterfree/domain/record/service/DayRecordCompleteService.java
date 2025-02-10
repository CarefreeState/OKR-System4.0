package cn.bitterfree.domain.record.service;

import cn.bitterfree.domain.record.model.entity.DayRecord;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:42
 */
public interface DayRecordCompleteService {

    void handle(DayRecord dayRecord, Boolean isCompleted, Boolean oldCompleted);

}
