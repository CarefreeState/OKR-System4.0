package cn.lbcmmszdntnt.domain.record.listener;

import cn.lbcmmszdntnt.domain.core.constants.FanoutExchangeConstants;
import cn.lbcmmszdntnt.domain.core.model.message.operate.KeyResultUpdate;
import cn.lbcmmszdntnt.domain.core.model.message.operate.StatusFlagUpdate;
import cn.lbcmmszdntnt.domain.core.model.message.operate.TaskUpdate;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.record.constants.DayRecordConstants;
import cn.lbcmmszdntnt.domain.record.factory.DayaRecordCompleteServiceFactory;
import cn.lbcmmszdntnt.domain.record.model.entity.DayRecord;
import cn.lbcmmszdntnt.domain.record.service.DayRecordService;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 19:02
 */
@Component
@RequiredArgsConstructor
@Slf4j
// 不必担心并发问题（因为只有创建者才能操作 okr）
public class OkrDayRecordMessageListener {

    private final RedisCache redisCache;

    private final OkrCoreService okrCoreService;

    private final DayRecordService dayRecordService;

    private final DayaRecordCompleteServiceFactory dayaRecordCompleteServiceFactory;

    private Boolean checkOkrIsOver(Long coreId) {
        return okrCoreService.getOkrCore(coreId).getIsOver();
    }

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = FanoutExchangeConstants.KEY_RESULT_UPDATE_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(name = FanoutExchangeConstants.KEY_RESULT_UPDATE_DAY_RECORD_QUEUE)
    ))
    public void keyResultUpdateDayRecordEventListener(KeyResultUpdate keyResultUpdate) {
        Long coreId = keyResultUpdate.getCoreId();
        if(Boolean.TRUE.equals(checkOkrIsOver(coreId))) {
            log.info("OKR 已结束不记录");
            return;
        }
        // 拿到今天的 redis key
        String redisKey = dayRecordService.todayRedisKey(coreId);
        // 若不存在今天的记录，就创建
        DayRecord dayRecord = dayRecordService.tryInitDayRecord(coreId);
        dayRecord.setCredit1(dayRecordService.recordFirstQuadrant(coreId));
        redisCache.setObject(redisKey, dayRecord, DayRecordConstants.DAY_RECORD_DATE_CACHE_TIMEOUT, DayRecordConstants.DAY_RECORD_DATE_CACHE_TIMEUNIT);
    }

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = FanoutExchangeConstants.TASK_UPDATE_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(name = FanoutExchangeConstants.TASK_UPDATE_DAY_RECORD_QUEUE)
    ))
    public void taskUpdateDayRecordEventListener(TaskUpdate taskUpdate) {
        Long coreId = taskUpdate.getCoreId();
        if(Boolean.TRUE.equals(checkOkrIsOver(coreId))) {
            log.info("OKR 已结束不记录");
            return;
        }
        String redisKey = dayRecordService.todayRedisKey(coreId);
        DayRecord dayRecord = dayRecordService.tryInitDayRecord(coreId);
        dayaRecordCompleteServiceFactory.getService(taskUpdate.getTaskType()).handle(
                dayRecord, taskUpdate.getIsCompleted(), taskUpdate.getOldCompleted()
        );
        redisCache.setObject(redisKey, dayRecord, DayRecordConstants.DAY_RECORD_DATE_CACHE_TIMEOUT, DayRecordConstants.DAY_RECORD_DATE_CACHE_TIMEUNIT);
    }

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = FanoutExchangeConstants.STATUS_FLAG_UPDATE_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(name = FanoutExchangeConstants.STATUS_FLAG_UPDATE_DAY_RECORD_QUEUE)
    ))
    public void statusFlagUpdateDayRecordEventListener(StatusFlagUpdate statusFlagUpdate) {
        Long coreId = statusFlagUpdate.getCoreId();
        if(Boolean.TRUE.equals(checkOkrIsOver(coreId))) {
            log.info("OKR 已结束不记录");
            return;
        }
        String redisKey = dayRecordService.todayRedisKey(coreId);
        DayRecord dayRecord = dayRecordService.tryInitDayRecord(coreId);
        dayRecord.setCredit4(dayRecordService.recordFourthQuadrant(coreId));
        redisCache.setObject(redisKey, dayRecord, DayRecordConstants.DAY_RECORD_DATE_CACHE_TIMEOUT, DayRecordConstants.DAY_RECORD_DATE_CACHE_TIMEUNIT);
    }

}
