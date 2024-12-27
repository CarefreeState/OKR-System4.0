package cn.lbcmmszdntnt.domain.record.service.impl;


import cn.lbcmmszdntnt.domain.record.model.entry.ActionUpdate;
import cn.lbcmmszdntnt.domain.record.service.DayRecordCompleteService;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:46
 */
@Service
public class DayRecordServiceThirdImpl implements DayRecordCompleteService {
    @Override
    public Object getEvent(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        return ActionUpdate.builder().coreId(coreId).isCompleted(isCompleted).oldCompleted(oldCompleted).build();
    }
}
