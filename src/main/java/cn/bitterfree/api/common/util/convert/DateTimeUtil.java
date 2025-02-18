package cn.bitterfree.api.common.util.convert;

import cn.bitterfree.api.common.constants.DateTimeConstants;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 19:45
 */
@Slf4j
public class DateTimeUtil {

    public static String getDateFormat(Date date) {
        return Optional.ofNullable(date).map(d -> new SimpleDateFormat(DateTimeConstants.DATE_TIME_PATTERN).format(d)).orElse(null);
    }

    public static String getOnlyDateFormat(Date date) {
        return Optional.ofNullable(date).map(d -> new SimpleDateFormat(DateTimeConstants.DATE_PATTERN).format(d)).orElse(null);
    }

    public static Date beginOfDay(Date date) {
        return Optional.ofNullable(date).map(DateUtil::beginOfDay).orElse(null);
    }

    public static void log(long delay, TimeUnit timeUnit) {
        long deadline = timeUnit.toMillis(delay) + System.currentTimeMillis();
        log.warn("计时开始，将于 “ {} ” {} 后执行，即于 {} 执行！", delay, timeUnit.name(),
                getDateFormat(new Date(deadline)));
    }
}
