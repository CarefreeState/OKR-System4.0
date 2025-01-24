package cn.lbcmmszdntnt.common.util.juc.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
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

    private static final ThreadPoolExecutor EXECUTOR = ThreadPoolUtil.getIoTargetThreadPool("OKR-System-Thread");
    private static final int DEFAULT_TASK_NUMBER = 30; // 在分批处理任务时默认的任务数

    public static void submit(Runnable... tasks) {
        // 提交任务
        Arrays.stream(tasks).forEach(IOThreadPool::submit);
    }

    public static void submit(Runnable runnable) {
        EXECUTOR.submit(runnable);
    }

    public static <T> void operateBatch(List<T> dataList, Consumer<List<T>> subListConsumer) {
        ThreadPoolUtil.operateBatch(dataList, subListConsumer, DEFAULT_TASK_NUMBER, EXECUTOR);
    }

}
