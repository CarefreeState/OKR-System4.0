package cn.lbcmmszdntnt.xxljob.service.impl;

import cn.lbcmmszdntnt.common.util.convert.ObjectUtil;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import cn.lbcmmszdntnt.xxljob.config.Executor;
import cn.lbcmmszdntnt.xxljob.constants.XxlJobConstants;
import cn.lbcmmszdntnt.xxljob.model.dto.GroupPageListDTO;
import cn.lbcmmszdntnt.xxljob.model.dto.JobGroupDTO;
import cn.lbcmmszdntnt.xxljob.model.entity.XxlJobGroup;
import cn.lbcmmszdntnt.xxljob.service.JobGroupService;
import cn.lbcmmszdntnt.xxljob.util.XxlJobRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
        String lock = String.format(XxlJobConstants.XXL_JOB_GROUP_LOCK, executor.getAppname(), executor.getTitle());
        redisLock.tryLockDoSomething(lock, () -> {
            String appname = executor.getAppname();
            String title = executor.getTitle();
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