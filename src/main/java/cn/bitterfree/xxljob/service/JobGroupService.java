package cn.bitterfree.xxljob.service;

import cn.bitterfree.xxljob.model.entity.XxlJobGroup;

import java.util.List;

public interface JobGroupService {

    List<XxlJobGroup> getJobGroup();

    XxlJobGroup getJobGroup(int index);

    void saveOrUpdateJobGroup();

}
