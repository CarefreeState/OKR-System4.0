package cn.lbcmmszdntnt.xxljob.service;

import cn.lbcmmszdntnt.xxljob.model.entity.XxlJobGroup;

import java.util.List;

public interface JobGroupService {

    List<XxlJobGroup> getJobGroup();

    XxlJobGroup getJobGroup(int index);

    void saveOrUpdateJobGroup();

}
