package cn.lbcmmszdntnt.xxljob.service.impl;

import cn.lbcmmszdntnt.xxljob.model.dto.InfoPageListDTO;
import cn.lbcmmszdntnt.xxljob.model.entity.XxlJobInfo;
import cn.lbcmmszdntnt.xxljob.service.JobInfoService;
import cn.lbcmmszdntnt.xxljob.util.XxlJobRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobInfoServiceImpl implements JobInfoService {

    @Override
    public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {
        InfoPageListDTO infoPageListDTO = InfoPageListDTO.builder()
                .jobGroup(jobGroupId)
                .executorHandler(executorHandler)
                .triggerStatus(-1)
                .build();
        return XxlJobRequestUtil.infoPageList(infoPageListDTO);
    }

    @Override
    public void addJob(XxlJobInfo xxlJobInfo) {
        log.warn("提交任务 {}", xxlJobInfo);
        XxlJobRequestUtil.infoAdd(xxlJobInfo);
    }

}