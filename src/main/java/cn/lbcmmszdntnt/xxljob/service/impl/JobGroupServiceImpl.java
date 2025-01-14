package cn.lbcmmszdntnt.xxljob.service.impl;

import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import cn.lbcmmszdntnt.xxljob.config.Executor;
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
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobGroupServiceImpl implements JobGroupService {

    private final static String XXL_JOB_GROUP = "xxlJobGroup:%s:%s";

    private final static String XXL_JOB_GROUP_LOCK = "xxlJobGroupLock:%s:%s";

    private final static Long XXL_JOB_GROUP_TTL = 1L;

    private final static TimeUnit XXL_JOB_GROUP_TIMEUNIT = TimeUnit.DAYS;

    private final Executor executor;

    private final RedisCache redisCache;

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
        addJobGroup();
        return getJobGroup().getFirst();
    }

    @Override
    public void addJobGroup() {
        String lock = String.format(XXL_JOB_GROUP_LOCK, executor.getAppname(), executor.getTitle());
        redisLock.tryLockDoSomething(lock, () -> {
            if (Boolean.FALSE.equals(preciselyCheck())) {
                autoRegisterGroup();
            }
        }, () -> {});
    }

    @Override
    public void autoRegisterGroup() {
        String appname = executor.getAppname();
        String title = executor.getTitle();
        GroupSaveDTO groupSaveDTO = GroupSaveDTO.builder()
                .appname(appname)
                .title(title)
                .addressType(executor.getAddressType())
                .addressList(executor.getAddressList())
                .build();
        XxlJobRequestUtil.groupSave(groupSaveDTO);
        String redisKey = String.format(XXL_JOB_GROUP, appname, title);
        redisCache.deleteObject(redisKey);
    }

    @Override
    public boolean preciselyCheck() {
        String appname = executor.getAppname();
        String title = executor.getTitle();
        String redisKey = String.format(XXL_JOB_GROUP, appname, title);
        return redisCache.getObject(redisKey, Boolean.class).orElseGet(() -> {
            boolean flag = getJobGroup().stream().anyMatch(Objects::nonNull);
            redisCache.setObject(redisKey, flag, XXL_JOB_GROUP_TTL, XXL_JOB_GROUP_TIMEUNIT);
            return flag;
        });
    }

}