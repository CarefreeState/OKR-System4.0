package cn.bitterfree.api.common.util.juc.threadpool;

import cn.bitterfree.api.common.util.convert.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SchedulerThreadPool {

    private static final AtomicLong THEAD_ID = new AtomicLong(1);
    private static final int SYSTEM_CORE_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = SYSTEM_CORE_SIZE * 2 + 1;
    private static final int MAXIMUM_POOL_SIZE = SYSTEM_CORE_SIZE * 3;
    private static final int KEEP_ALIVE_TIME = 3;
    private static final TimeUnit KEEP_ALIVE_TIMEUNIT = TimeUnit.SECONDS;
    private static final BlockingDeque<Runnable> BLOCKING_DEQUE = null; // null 代表使用默认的阻塞队列
    private static final ThreadFactory THREAD_FACTORY = r -> new Thread(r, "OKR-System-Thread-Scheduler-IO" + THEAD_ID.getAndIncrement());
    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();
    private static final ScheduledExecutorService THREAD_POOL;
    private static final int AWAIT_TIME = 5;
    private static final TimeUnit AWAIT_TIMEUNIT = TimeUnit.SECONDS;

    static {
        THREAD_POOL = new CustomScheduledExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIMEUNIT,
                BLOCKING_DEQUE,
                THREAD_FACTORY,
                REJECTED_EXECUTION_HANDLER
        );
    }

    // 添加普通定时任务
    public static void schedule(Runnable task, long delay, TimeUnit unit) {
        DateTimeUtil.log(delay, unit);
        THREAD_POOL.schedule(task, delay, unit);
    }

    // 关闭线程池
    public static void shutdown() {
        THREAD_POOL.shutdown();
        try {
            if (!THREAD_POOL.awaitTermination(AWAIT_TIME, AWAIT_TIMEUNIT)) {
                THREAD_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            THREAD_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void remove(Runnable task) {
        ((ScheduledThreadPoolExecutor) THREAD_POOL).remove(task);
    }

    public static class CustomScheduledExecutor extends ScheduledThreadPoolExecutor {

        private final BlockingQueue<Runnable> customQueue;

        public CustomScheduledExecutor(int corePoolSize,
                                       int maximumPoolSize,
                                       long keepAliveTime,
                                       TimeUnit unit,
                                       BlockingQueue<Runnable> workQueue,
                                       ThreadFactory threadFactory,
                                       RejectedExecutionHandler handler) {
            super(corePoolSize, threadFactory, handler);
            this.setMaximumPoolSize(maximumPoolSize);
            this.setKeepAliveTime(keepAliveTime, unit);
            this.customQueue = Objects.isNull(workQueue) ? super.getQueue() : workQueue;
        }

        @Override
        public BlockingQueue<Runnable> getQueue() {
            return customQueue;
        }
    }
}
