package cn.lbcmmszdntnt.domain.record.config;

import cn.lbcmmszdntnt.common.util.convert.DateTimeUtil;
import cn.lbcmmszdntnt.common.util.thread.pool.IOThreadPool;
import cn.lbcmmszdntnt.domain.record.constants.DayRecordConstants;
import cn.lbcmmszdntnt.domain.record.model.entity.DayRecord;
import cn.lbcmmszdntnt.domain.record.service.DayRecordService;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static int TRIGGER_STATUS = 1;

    private final static String CRON = "0 0 0 * * ? *"; // 每天 0 点

    private final RedisCache redisCache;

    private final DayRecordService dayRecordService;

    // 渐入佳境勋章
    @XxlJob(value = "pageOutDayRecord")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR,  triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】每天一次的写入昨日 OKR 日记录的任务")
    public void pageOutDayRecord() {
        String yesterday = DateTimeUtil.getDateFormat(new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));
        List<DayRecord> dayRecordList = new ArrayList<>();
        // 数据量很大，需要分批处理
        Set<String> keys = redisCache.getKeysByPrefix(DayRecordConstants.DAY_RECORD_DATE_CACHE_PREFIX);
        IOThreadPool.operateBatch(keys.stream().toList(), redisKey -> {
            redisCache.getObject(redisKey, DayRecord.class).ifPresent(dayRecordList::add);
        });
        redisCache.deleteObjects(keys);
        dayRecordService.saveBatch(dayRecordList);
        log.info("{} 的日记录已成功写入数据库，共 {} 条", yesterday, dayRecordList.size());
    }


}
