package cn.bitterfree.api.common.util.juc.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 14:04
 */
@Slf4j
public class IOThreadPool {

    private final static ThreadPoolExecutor EXECUTOR = ThreadPoolUtil.getIoTargetThreadPool("OKR-System-Thread");
    private final static int DEFAULT_TASK_NUMBER = 30; // 在分批处理任务时默认的任务数

    public static void submit(Runnable runnable) {
        EXECUTOR.submit(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return EXECUTOR.submit(callable);
    }

    public static <T> void operateBatch(List<T> dataList, Consumer<List<T>> subListConsumer) {
        ThreadPoolUtil.operateBatch(dataList, subListConsumer, DEFAULT_TASK_NUMBER, EXECUTOR);
    }

}
