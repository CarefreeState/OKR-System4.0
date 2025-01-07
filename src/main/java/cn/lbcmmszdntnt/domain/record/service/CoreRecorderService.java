package cn.lbcmmszdntnt.domain.record.service;


import cn.lbcmmszdntnt.domain.record.model.entity.CoreRecorder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 马拉圈
* @description 针对表【core_recorder(OKR 内核记录器表)】的数据库操作Service
* @createDate 2024-04-21 02:02:14
*/
public interface CoreRecorderService extends IService<CoreRecorder> {

    void initRecordMap(CoreRecorder coreRecorder, Long coreId);

    CoreRecorder initCoreRecorder(Long coreId);

    CoreRecorder getCoreRecorder(Long coreId);

    void removeCoreRecorderCache(Long coreId);

}
