package cn.bitterfree.api.xxljob.service.impl;

import cn.bitterfree.api.redis.lock.RedisLock;
import cn.bitterfree.api.xxljob.constants.XxlJobConstants;
import cn.bitterfree.api.xxljob.model.dto.InfoPageListDTO;
import cn.bitterfree.api.xxljob.model.entity.XxlJobInfo;
import cn.bitterfree.api.xxljob.service.JobInfoService;
import cn.bitterfree.api.xxljob.util.XxlJobRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobInfoServiceImpl implements JobInfoService {

    private final RedisLock redisLock;

    @Override
    public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {
        InfoPageListDTO infoPageListDTO = InfoPageListDTO.builder()
                .jobGroup(jobGroupId)
                .executorHandler(executorHandler)
                .triggerStatus(-1)
                .jobDesc("")
                .author("")
                .build();
        return XxlJobRequestUtil.infoPageList(infoPageListDTO);
    }

    @Override
    public void saveOrUpdateJobInfo(XxlJobInfo xxlJobInfo) {
        int jobGroup = xxlJobInfo.getJobGroup();
        String executorHandler = xxlJobInfo.getExecutorHandler();
        String lock = String.format(XxlJobConstants.XXL_JOB_INFO_LOCK, jobGroup, executorHandler);
        redisLock.tryLockDoSomething(lock, () -> {
            getJobInfo(jobGroup, executorHandler).stream()
                    .filter(info -> info.getExecutorHandler().equals(executorHandler)) // 因为是模糊查询，需要再判断一次
                    .findFirst()
                    .ifPresentOrElse(info -> {
                        xxlJobInfo.setId(info.getId());
                        XxlJobRequestUtil.infoUpdate(xxlJobInfo);
                    }, () -> {
                        XxlJobRequestUtil.infoAdd(xxlJobInfo);
                    });
        }, () -> {});
    }

}