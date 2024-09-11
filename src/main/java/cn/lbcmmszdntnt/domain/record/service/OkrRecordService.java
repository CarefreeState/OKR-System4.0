package cn.lbcmmszdntnt.domain.record.service;


import cn.lbcmmszdntnt.domain.record.model.po.CoreRecorder;
import cn.lbcmmszdntnt.domain.record.model.po.ext.Record;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-23
 * Time: 21:01
 */
public interface OkrRecordService {

    Record createNewRecord(Long coreId);

    /**
     * 此方法可以让 coreRecorder 的 recordMap 存在，且指向最新的 record
     * @param coreRecorder
     * @return
     */
    Record switchRecord(CoreRecorder coreRecorder);

    Record getNowRecord(Long coreId);

    @Transactional
    List<Record> getRecords(Long coreId);

}
