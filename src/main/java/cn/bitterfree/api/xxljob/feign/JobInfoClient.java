package cn.bitterfree.api.xxljob.feign;

import cn.bitterfree.api.xxljob.model.entity.XxlJobInfo;
import cn.bitterfree.api.xxljob.model.vo.InfoPageListVO;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-03-05
 * Time: 10:35
 */
@FeignClient(name = "jobinfo-service", url = "${okr.xxl-job.admin.addresses}/jobinfo")
public interface JobInfoClient {

    @PostMapping("/pageList")
    InfoPageListVO pageList(@RequestHeader(HttpHeaders.COOKIE) String cookie,
                            @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                            @RequestParam(value = "length", required = false, defaultValue = "10") Integer length,
                            @RequestParam("jobGroup") Integer jobGroup,
                            @RequestParam("triggerStatus") Integer triggerStatus,
                            @RequestParam("jobDesc") String jobDesc,
                            @RequestParam("executorHandler") String executorHandler,
                            @RequestParam("author") String author);

    @PostMapping("/add")
    ReturnT<String> add(@RequestHeader(HttpHeaders.COOKIE) String cookie, XxlJobInfo jobInfo);

    @PostMapping("/update")
    ReturnT<String> update(@RequestHeader(HttpHeaders.COOKIE) String cookie, XxlJobInfo jobInfo);

}
