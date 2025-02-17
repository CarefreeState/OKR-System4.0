package cn.bitterfree.api.xxljob.service;

import cn.bitterfree.api.xxljob.model.entity.XxlJobInfo;

import java.util.List;

public interface JobInfoService {

    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler);

    void saveOrUpdateJobInfo(XxlJobInfo xxlJobInfo);

}
