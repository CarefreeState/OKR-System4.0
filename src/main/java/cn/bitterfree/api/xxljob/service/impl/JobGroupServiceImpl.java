package cn.bitterfree.api.xxljob.service.impl;

import cn.bitterfree.api.common.util.convert.ObjectUtil;
import cn.bitterfree.api.redis.lock.RedisLock;
import cn.bitterfree.api.xxljob.config.Executor;
import cn.bitterfree.api.xxljob.constants.XxlJobConstants;
import cn.bitterfree.api.xxljob.cookie.XxlJobCookie;
import cn.bitterfree.api.xxljob.feign.JobGroupClient;
import cn.bitterfree.api.xxljob.model.entity.XxlJobGroup;
import cn.bitterfree.api.xxljob.service.JobGroupService;
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

    private final JobGroupClient jobGroupClient;

    @Override
    public List<XxlJobGroup> getJobGroup() {
        return jobGroupClient.pageList(XxlJobCookie.getXxlJobCookie().getCookie(), null, null,
                executor.getAppname(), executor.getTitle()).getData();
    }

    @Override
    public XxlJobGroup getJobGroup(int index) {
        return getJobGroup().get(index);
    }

    @Override
    public void saveOrUpdateJobGroup() {
        String appname = executor.getAppname();
        String title = executor.getTitle();
        redisLock.tryLockDoSomething(String.format(XxlJobConstants.XXL_JOB_GROUP_LOCK, appname, title), () -> {
            XxlJobGroup jobGroup = XxlJobGroup.builder()
                    .appname(appname)
                    .title(title)
                    .addressType(executor.getAddressType())
                    .addressList(executor.getAddressList())
                    .build();
            // 判断是否更新 group
            String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
            ObjectUtil.nonNullstream(getJobGroup()).findFirst().ifPresentOrElse(group -> {
                jobGroup.setId(group.getId());
                jobGroupClient.update(cookie, jobGroup);
            }, () -> {
                jobGroupClient.save(cookie, jobGroup);
            });
        }, () -> {});
    }

}