package cn.lbcmmszdntnt.xxljob.service;

import cn.lbcmmszdntnt.xxljob.model.XxlJobInfo;

import java.util.List;

public interface JobInfoService {

    List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler);

    Integer addJob(XxlJobInfo xxlJobInfo);

}
