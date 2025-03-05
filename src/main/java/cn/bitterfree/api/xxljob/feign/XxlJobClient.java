package cn.bitterfree.api.xxljob.feign;

import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-03-05
 * Time: 9:52
 */
@FeignClient(name = "xxljob-service", url = "${okr.xxl-job.admin.addresses}")
public interface XxlJobClient {

    @PostMapping("/login")
    ResponseEntity<ReturnT<String>> loginDo(@RequestParam("userName") String userName,
                                            @RequestParam("password") String password,
                                            @RequestParam(value = "ifRemember", required = false) String ifRemember);

}
