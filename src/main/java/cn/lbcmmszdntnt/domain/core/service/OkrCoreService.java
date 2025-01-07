package cn.lbcmmszdntnt.domain.core.service;


import cn.lbcmmszdntnt.domain.core.model.entity.OkrCore;
import cn.lbcmmszdntnt.domain.core.model.vo.OkrCoreVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
* @author 马拉圈
* @description 针对表【okr_core(OKR 内核表)】的数据库操作Service
* @createDate 2024-01-22 13:42:11
*/
public interface OkrCoreService extends IService<OkrCore> {

    @Transactional
    Long createOkrCore();

    OkrCore getOkrCore(Long coreId);

    void checkOverThrows(Long coreId);

    void checkNonOverThrows(Long coreId);

    void removeOkrCoreCache(Long coreId);

    OkrCoreVO searchOkrCore(Long id);

    void confirmCelebrateDate(Long id, Integer celebrateDay);

    Date summaryOKR(Long id, String summary, Integer degree);

    void complete(Long id);

    void checkThirdCycle(Long id, Integer quadrantCycle);

}
