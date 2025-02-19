package cn.bitterfree.api.domain.record.config;

import cn.bitterfree.api.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.api.domain.record.model.entity.DayRecord;
import cn.bitterfree.api.domain.record.service.DayRecordService;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 3:36
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DayRecordPageOutXxlJobConfig {

    private final static String ROUTE = "ROUND";
    private final static int TRIGGER_STATUS = 1;
    private final static String CRON = "0 0 0 * * ? *"; // 每天 0 点

    private final RedisCache redisCache;

    private final DayRecordService dayRecordService;

    // 渐入佳境勋章
    @XxlJob(value = "pageOutDayRecord")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE, triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】每天一次的昨日 OKR 日记录缓存写入数据库")
    public void pageOutDayRecord() {
        // 数据量很大，需要分批处理
        String yesterday = dayRecordService.yesterdayRedisKeyPrefix();
        Set<String> keys = redisCache.getKeysByPrefix(yesterday);
        List<DayRecord> dayRecordList = new ArrayList<>(keys.size());
        IOThreadPool.operateBatch(keys.stream().toList(), redisKeyList -> {
            redisKeyList.forEach(redisKey -> {
                redisCache.getObject(redisKey, DayRecord.class).ifPresent(dayRecordList::add);
            });
        });
        redisCache.deleteObjects(keys);
        dayRecordService.saveBatch(dayRecordList);
        log.info("{} 的日记录已成功写入数据库，共 {} 条", yesterday, dayRecordList.size());
    }


}
