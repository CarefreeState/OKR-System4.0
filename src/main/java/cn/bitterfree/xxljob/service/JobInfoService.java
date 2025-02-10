package cn.bitterfree.xxljob.service;

import cn.bitterfree.xxljob.model.entity.XxlJobInfo;

import java.util.List;

public interface JobInfoService {

    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler);

    void saveOrUpdateJobInfo(XxlJobInfo xxlJobInfo);

}
