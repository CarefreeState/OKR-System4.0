package cn.lbcmmszdntnt.xxljob.service.impl;

import cn.lbcmmszdntnt.redis.lock.RedisLock;
import cn.lbcmmszdntnt.xxljob.config.Executor;
import cn.lbcmmszdntnt.xxljob.constants.XxlJobGroupConstants;
import cn.lbcmmszdntnt.xxljob.model.dto.GroupPageListDTO;
import cn.lbcmmszdntnt.xxljob.model.dto.GroupSaveDTO;
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
    public XxlJobGroup getJobGroupOne(int index) {
        return getJobGroup().get(index);
    }

    @Override
    public void addJobGroup() {
        String lock = String.format(XxlJobGroupConstants.XXL_JOB_GROUP_LOCK, executor.getAppname(), executor.getTitle());
        redisLock.tryLockDoSomething(lock, () -> {
            if (getJobGroup().stream().noneMatch(Objects::nonNull)) {
                String appname = executor.getAppname();
                String title = executor.getTitle();
                GroupSaveDTO groupSaveDTO = GroupSaveDTO.builder()
                        .appname(appname)
                        .title(title)
                        .addressType(executor.getAddressType())
                        .addressList(executor.getAddressList())
                        .build();
                XxlJobRequestUtil.groupSave(groupSaveDTO);
            }
        }, () -> {});
    }

}