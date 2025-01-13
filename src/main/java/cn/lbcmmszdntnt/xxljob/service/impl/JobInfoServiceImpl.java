package cn.lbcmmszdntnt.xxljob.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.xxljob.config.Admin;
import cn.lbcmmszdntnt.xxljob.config.XxlUrl;
import cn.lbcmmszdntnt.xxljob.cookie.CookieUtil;
import cn.lbcmmszdntnt.xxljob.model.XxlJobInfo;
import cn.lbcmmszdntnt.xxljob.service.JobInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobInfoServiceImpl implements JobInfoService {

    private final Admin admin;

    private final XxlUrl xxlUrl;

    @Override
    public List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {
        String url = admin.getAddresses() + xxlUrl.getInfoPageList();
        HttpResponse response = HttpRequest.post(url)
                .form("jobGroup", jobGroupId)
                .form("executorHandler", executorHandler)
                .form("triggerStatus", -1)
                .cookie(CookieUtil.getCookie())
                .execute();

        String body = response.body();
        JSONArray array = JsonUtil.analyzeJsonField(body, "data", JSONArray.class);
        List<XxlJobInfo> list = array.stream()
                .map(o -> JSONUtil.toBean((JSONObject) o, XxlJobInfo.class))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public Integer addJob(XxlJobInfo xxlJobInfo) {
        log.warn("提交任务 {}", xxlJobInfo);
        String url = admin.getAddresses() + xxlUrl.getInfoAdd();
        Map<String, Object> paramMap = BeanUtil.beanToMap(xxlJobInfo);
        HttpResponse response = HttpRequest.post(url)
                .form(paramMap)
                .cookie(CookieUtil.getCookie())
                .execute();
        String body = response.body();
        Integer code = JsonUtil.analyzeJsonField(body, "code", Integer.class);
        if (code.equals(200)) {
            return JsonUtil.analyzeJsonField(body, "content", Integer.class);
        }else {
            throw new GlobalServiceException("add jobInfo error!");
        }
    }

}