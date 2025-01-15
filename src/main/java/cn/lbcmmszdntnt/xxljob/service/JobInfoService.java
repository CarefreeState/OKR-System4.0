package cn.lbcmmszdntnt.xxljob.service;

import cn.lbcmmszdntnt.xxljob.model.entity.XxlJobInfo;

import java.util.List;

public interface JobInfoService {

    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler);

    void addJob(XxlJobInfo xxlJobInfo);

}
