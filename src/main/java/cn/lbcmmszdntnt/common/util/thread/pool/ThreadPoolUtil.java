package cn.lbcmmszdntnt.common.util.thread.pool;

import cn.lbcmmszdntnt.exception.GlobalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
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

    public static <T> void operateBatch(List<T> dataList, Consumer<T> consumer,
                                        int defaultTaskNumber, int corePoolSize, ExecutorService threadPool) {
        if(CollectionUtils.isEmpty(dataList)) {
            return;
        }
        int size = dataList.size();
        // 计算多少个线程，每个线程多少个任务
        int taskNumber = defaultTaskNumber;
        int threadNumber = size / taskNumber;
        while (taskNumber * threadNumber < size) {
            threadNumber++;
        }
        if(threadNumber > corePoolSize) {
            threadNumber = corePoolSize;
            taskNumber = size / threadNumber;
        }
        while (taskNumber * threadNumber < size) {
            taskNumber++;
        }
        log.info("启动 {} 个线程，每个线程处理 {} 个任务", threadNumber, taskNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);
        for (int i = 0; i < size; i += taskNumber) {
            final int from = i;
            final int to = Math.min(i + taskNumber, size);
            threadPool.submit(() -> {
                log.info("分段操作 [{}, {})", from, to);
                dataList.subList(from, to).forEach(consumer);
                latch.countDown();
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
