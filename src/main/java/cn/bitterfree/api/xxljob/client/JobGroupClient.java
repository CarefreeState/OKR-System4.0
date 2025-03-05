package cn.bitterfree.api.xxljob.client;

import cn.bitterfree.api.xxljob.model.entity.XxlJobGroup;
import cn.bitterfree.api.xxljob.model.vo.GroupPageListVO;
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
 * Time: 10:34
 */
@FeignClient(name = "jobgroup-service", url = "${okr.xxl-job.admin.addresses}/jobgroup")
public interface JobGroupClient {

    @PostMapping("/pageList")
    GroupPageListVO pageList(@RequestHeader(HttpHeaders.COOKIE) String cookie,
                             @RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
                             @RequestParam(value = "length", required = false, defaultValue = "10") Integer length,
                             @RequestParam("appname") String appname,
                             @RequestParam("title") String title);

    @PostMapping("/save")
    ReturnT<String> save(@RequestHeader(HttpHeaders.COOKIE) String cookie, XxlJobGroup xxlJobGroup);

    @PostMapping("/update")
    ReturnT<String> update(@RequestHeader(HttpHeaders.COOKIE) String cookie, XxlJobGroup xxlJobGroup);

}
