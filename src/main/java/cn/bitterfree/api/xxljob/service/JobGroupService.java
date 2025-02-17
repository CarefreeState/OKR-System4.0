package cn.bitterfree.api.xxljob.service;

import cn.bitterfree.api.xxljob.model.entity.XxlJobGroup;

import java.util.List;

public interface JobGroupService {

    List<XxlJobGroup> getJobGroup();

    XxlJobGroup getJobGroup(int index);

    void saveOrUpdateJobGroup();

}
