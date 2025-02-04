package cn.lbcmmszdntnt.common.util.juc.threadpool;

import cn.lbcmmszdntnt.common.exception.GlobalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-18
 * Time: 10:45
 */
@Slf4j
public class ThreadPoolUtil {

    /**
     * 系统 CPU 核数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * IO 核心线程数
     */
    private static final int IO_CORE = Math.max(3, 2 * CPU_COUNT + 1);
    /**
     * IO 最大线程数
     */
    private static final int IO_MAX = Math.max(3, 3 * CPU_COUNT); // 最大线程数
    /**
     * 空闲线程最大保活时限，单位为秒
     */
    private static final int KEEP_ALIVE_SECOND = 60;
    /**
     * 有界阻塞队列容量上限
     */
    private static final int QUEUE_SIZE = 10_000;

    public static class ShutdownHookThread extends Thread {
        private volatile boolean hasShutdown = false;
        private final Callable<?> callback;

        /**
         * 创建标准钩子线程异步处理关闭线程池逻辑
         * @param name
         * @param callback 回调钩子方法
         */
        public ShutdownHookThread(String name, Callable<?> callback) {
            super("JVM关闭, 触发钩子函数处理(" + name + ")");
            this.callback = callback;
        }

        @Override
        public void run() {
            synchronized (this) {
                log.info(getName() + " 线程开始运转...");
                if (!this.hasShutdown) {
                    this.hasShutdown = true;
                    long beginTime = System.currentTimeMillis();
                    try {
                        this.callback.call();
                    } catch (Exception e) {
                        log.error(getName() + " 线程出现异常：", e.getMessage());
                    }
                    long consumingTimeTotal = System.currentTimeMillis() - beginTime;
                    log.info(getName() + " 耗时(s): " + consumingTimeTotal);
                }
            }
        }
    }

    /**
     * <p>看到一些开源代码是将线程池设置成懒汉式，只有等代码需要用到线程池再加载，
     * 我觉得并不是最正确的一种形式</p>
     * 原因是线程池应当在应用程序启动时就会初始化，因为它是一个全局资源，
     * 提前初始化可以避免再需要等待初始化的时间延迟<br />
     * 特别是在多线程环境下可能会导致竞态条件或者其他并发问题
     */
    private static class IoIntenseTargetThreadPoolHolder {

        private String threadName;

        private IoIntenseTargetThreadPoolHolder(String threadName) {
            this.threadName = threadName;
        }

        private final AtomicInteger NUM = new AtomicInteger(0);

        private final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                IO_CORE,
                IO_MAX,
                KEEP_ALIVE_SECOND,
                TimeUnit.SECONDS,
                // 任务队列存储超过核心线程数的任务
                new LinkedBlockingDeque<>(QUEUE_SIZE),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName("[" + threadName + "] message-process-thread-" + NUM.getAndIncrement());
                    return thread;
                }
        );

        {
            log.info("线程池已经初始化");
            EXECUTOR.allowCoreThreadTimeOut(true);
            // JVM 关闭时的钩子函数
            Runtime.getRuntime().addShutdownHook(
                    new ShutdownHookThread("IO 密集型任务线程池", (Callable<Void>) () -> {
                        shutdownThreadPoolGracefully(EXECUTOR);
                        return null;
                    })
            );
        }

    }

    public static ThreadPoolExecutor getIoTargetThreadPool(String threadPool) {
        return new IoIntenseTargetThreadPoolHolder(threadPool).EXECUTOR;
    }

    private static void shutdownThreadPoolGracefully(ThreadPoolExecutor threadPool) {
        // 如果已经关闭则返回
        if (!(threadPool instanceof ExecutorService) || threadPool.isTerminated()) {
            return;
        }
        try {
            // 拒绝接收新任务，线程池状态变成 SHUTDOWN
            threadPool.shutdown();
        } catch (SecurityException | NullPointerException e) {

            return;
        }
        try {
            // 等待 60 秒，用户程序主动调用 awaitTermination 等待线程池的任务执行完毕
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                // 调用 shutdownNow() 强制停止所有任务，线程池状态变成 STOP
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.out.println("线程池任务未执行完毕！");
                }
            }
        } catch (InterruptedException e) {
            // 捕获异常，重新调用 shutdownNow() 方法
            threadPool.shutdownNow();
        }
        // 仍然没有关闭，循环关闭 1000 次，每次等待 10 毫秒
        int loopCount = 1000;
        if (!threadPool.isTerminated()) {
            try {
                for (int i = 0; i < loopCount; i++) {
                    if (threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                    threadPool.shutdownNow();
                }
            } catch (Throwable e) {
                log.error(e.getMessage());
            }
        }
    }

    public static <T> void operateBatch(List<T> dataList, Consumer<List<T>> subListConsumer,
                                        int defaultTaskNumber, ThreadPoolExecutor threadPool) {
        if(CollectionUtils.isEmpty(dataList)) {
            log.info("共需处理 0 个数据");
            return;
        }
        int size = dataList.size();
        // 计算多少个线程，每个线程多少个任务
        int taskNumber = defaultTaskNumber;
        int threadNumber = size / taskNumber;
        // 保证整体任务数达到待处理的数据数
        while (taskNumber * threadNumber < size) {
            threadNumber++;
        }
        // 控制线程数在核心线程数以内
        int coreSize = threadPool.getCorePoolSize();
        if(threadNumber > coreSize) {
            threadNumber = coreSize;
            taskNumber = size / threadNumber;
        }
        // 保证整体任务数达到待处理的数据数
        while (taskNumber * threadNumber < size) {
            taskNumber++;
        }
        log.info("启动 {} 个线程，每个线程处理 {} 个任务，共需处理 {} 个数据", threadNumber, taskNumber, size);
        CountDownLatch latch = new CountDownLatch(threadNumber);
        for (int i = 0; i < size; i += taskNumber) {
            int from = i;
            int to = Math.min(i + taskNumber, size);
            threadPool.submit(() -> {
                try {
                    log.info("分段操作 [{}, {})", from, to);
                    subListConsumer.accept(dataList.subList(from, to));
                } catch (Exception e) {
                    log.warn(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
            log.info("分段批量操作执行完毕");
        } catch (InterruptedException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

}
