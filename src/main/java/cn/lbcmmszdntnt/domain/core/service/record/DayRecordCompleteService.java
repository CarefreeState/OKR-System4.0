package cn.lbcmmszdntnt.domain.core.service.record;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:42
 */
public interface DayRecordCompleteService {

    void handle(Long coreId, Boolean isCompleted, Boolean oldCompleted);

}
