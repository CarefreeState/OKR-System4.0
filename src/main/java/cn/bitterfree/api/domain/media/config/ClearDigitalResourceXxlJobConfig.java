package cn.bitterfree.api.domain.media.config;

import cn.bitterfree.api.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.api.domain.media.model.entity.DigitalResource;
import cn.bitterfree.api.domain.media.service.DigitalResourceService;
import cn.bitterfree.api.domain.media.service.ObjectStorageService;
import cn.bitterfree.api.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 17:41
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ClearDigitalResourceXxlJobConfig {

    private final static String ROUTE = "ROUND";
    private final static String CRON = "0 0 * * * ? *"; // 每小时
    private final static int RIGGER_STATUS = 1;

    private final DigitalResourceService digitalResourceService;

    private final ObjectStorageService objectStorageService;

    @XxlJob(value = "clearDigitalResource")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE, triggerStatus = RIGGER_STATUS, jobDesc = "【固定任务】每小时一次的清除不活跃资源")
    @Transactional
    public void clearDigitalResource() {
        long now = System.currentTimeMillis();
        List<DigitalResource> list = digitalResourceService.lambdaQuery()
                .ge(DigitalResource::getActiveLimit, 0L)
                .list()
                .stream()
                .filter(digitalResource -> digitalResource.getActiveLimit().compareTo(now - digitalResource.getUpdateTime().getTime()) <= 0)
                .toList();
        IOThreadPool.operateBatch(
                list.stream().map(DigitalResource::getCode).toList(),
                digitalResourceService::removeResource
        );
        IOThreadPool.operateBatch(
                list.stream().map(DigitalResource::getFileName).toList(),
                objectStorageService::remove
        );
    }

}
