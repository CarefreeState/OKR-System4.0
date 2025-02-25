package cn.bitterfree.api.xxljob.service.impl;

import cn.bitterfree.api.common.util.convert.ObjectUtil;
import cn.bitterfree.api.redis.lock.RedisLock;
import cn.bitterfree.api.xxljob.config.Executor;
import cn.bitterfree.api.xxljob.constants.XxlJobConstants;
import cn.bitterfree.api.xxljob.model.dto.GroupPageListDTO;
import cn.bitterfree.api.xxljob.model.dto.JobGroupDTO;
import cn.bitterfree.api.xxljob.model.entity.XxlJobGroup;
import cn.bitterfree.api.xxljob.service.JobGroupService;
import cn.bitterfree.api.xxljob.util.XxlJobRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobGroupServiceImpl implements JobGroupService {

    private final Executor executor;

    private final RedisLock redisLock;

    @Override
    public List<XxlJobGroup> getJobGroup() {
        GroupPageListDTO groupPageListDTO = GroupPageListDTO.builder()
                .appname(executor.getAppname())
                .title(executor.getTitle())
                .build();
        return XxlJobRequestUtil.groupPageList(groupPageListDTO);
    }

    @Override
    public XxlJobGroup getJobGroup(int index) {
        return getJobGroup().get(index);
    }

    @Override
    public void saveOrUpdateJobGroup() {
        String appname = executor.getAppname();
        String title = executor.getTitle();
        String lock = String.format(XxlJobConstants.XXL_JOB_GROUP_LOCK, appname, title);
        redisLock.tryLockDoSomething(lock, () -> {
            JobGroupDTO jobGroupDTO = JobGroupDTO.builder()
                    .appname(appname)
                    .title(title)
                    .addressType(executor.getAddressType())
                    .addressList(executor.getAddressList())
                    .build();
            // 判断是否更新 group
            ObjectUtil.nonNullstream(getJobGroup()).findFirst().ifPresentOrElse(group -> {
                jobGroupDTO.setId(group.getId());
                XxlJobRequestUtil.groupUpdate(jobGroupDTO);
            }, () -> {
                XxlJobRequestUtil.groupSave(jobGroupDTO);
            });
        }, () -> {});
    }

}