package cn.lbcmmszdntnt.domain.coredeadline.util;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-24
 * Time: 18:49
 */
public class DeadlineUtil {

    public static long getNextDeadline(long deadTimestamp, long nowTimestamp, int cycle, TimeUnit cycleUnit) {
        long gap = cycleUnit.toMillis(cycle);
        while(deadTimestamp <= nowTimestamp) {
            deadTimestamp += gap;
        }
        return deadTimestamp;
    }

}
