package cn.bitterfree.api.domain.record.constants;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 19:18
 */
public interface DayRecordConstants {

    String DAY_RECORD_DATE_CACHE_PREFIX = "dayRecordDateCache:%s:"; // date:
    Long DAY_RECORD_DATE_CACHE_TIMEOUT = 2L;
    TimeUnit DAY_RECORD_DATE_CACHE_TIMEUNIT = TimeUnit.DAYS;

}
