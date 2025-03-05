package cn.bitterfree.api.xxljob.service.impl;

import cn.bitterfree.api.redis.lock.RedisLock;
import cn.bitterfree.api.xxljob.constants.XxlJobConstants;
import cn.bitterfree.api.xxljob.cookie.XxlJobCookie;
import cn.bitterfree.api.xxljob.feign.JobInfoClient;
import cn.bitterfree.api.xxljob.model.entity.XxlJobInfo;
import cn.bitterfree.api.xxljob.service.JobInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobInfoServiceImpl implements JobInfoService {

    private final RedisLock redisLock;

    private final JobInfoClient jobInfoClient;

    @Override
    public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {
        return jobInfoClient.pageList(XxlJobCookie.getXxlJobCookie().getCookie(), null, null, jobGroupId,
                -1, "", executorHandler, "").getData();
    }

    @Override
    public void saveOrUpdateJobInfo(XxlJobInfo xxlJobInfo) {
        int jobGroup = xxlJobInfo.getJobGroup();
        String executorHandler = xxlJobInfo.getExecutorHandler();
        String lock = String.format(XxlJobConstants.XXL_JOB_INFO_LOCK, jobGroup, executorHandler);
        redisLock.tryLockDoSomething(lock, () -> {
            String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
            getJobInfo(jobGroup, executorHandler).stream()
                    .filter(info -> info.getExecutorHandler().equals(executorHandler)) // 因为是模糊查询，需要再判断一次
                    .findFirst()
                    .ifPresentOrElse(info -> {
                        xxlJobInfo.setId(info.getId());
                        jobInfoClient.update(cookie, xxlJobInfo);
                    }, () -> {
                        jobInfoClient.add(cookie, xxlJobInfo);
                    });
        }, () -> {});
    }

}