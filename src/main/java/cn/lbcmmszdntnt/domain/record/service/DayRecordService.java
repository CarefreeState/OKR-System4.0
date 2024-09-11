package cn.lbcmmszdntnt.domain.record.service;


import cn.lbcmmszdntnt.domain.record.model.po.DayRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 马拉圈
* @description 针对表【day_record(OKR 内核日记录表)】的数据库操作Service
* @createDate 2024-04-21 02:02:14
*/
public interface DayRecordService extends IService<DayRecord>, OkrRecordService {

    @Transactional
    void recordFirstQuadrant(Long coreId);

    @Transactional
    void recordSecondQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted);

    @Transactional
    void recordThirdQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted);

    @Transactional
    void recordFourthQuadrant(Long coreId);

}
