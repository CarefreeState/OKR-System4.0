package cn.lbcmmszdntnt.common.util.juc.timer;

import cn.lbcmmszdntnt.common.util.convert.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 22:05
 */
@Slf4j
public class TimerUtil {

    private final static Timer TIMER = new Timer();

    public static void log(long delay, TimeUnit timeUnit) {
        long deadline = timeUnit.toMillis(delay) + System.currentTimeMillis();
        log.warn("计时开始，将于 “ {} ” {} 后执行，即于 {} 执行！", delay, timeUnit.name(),
                DateTimeUtil.getDateFormat(new Date(deadline)));
    }

    public static void schedule(TimerTask timerTask, long delay, TimeUnit timeUnit) {
        log(delay, timeUnit);
        TIMER.schedule(timerTask, timeUnit.toMillis(delay));
    }

    public static void schedule(Runnable task, long delay, TimeUnit timeUnit) {
        schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, delay, timeUnit);
    }

}
