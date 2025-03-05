package cn.bitterfree.api.xxljob.cookie;

import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.common.util.web.HttpRequestUtil;
import cn.bitterfree.api.xxljob.client.XxlJobClient;
import cn.bitterfree.api.xxljob.config.Admin;
import cn.hutool.extra.spring.SpringUtil;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-11
 * Time: 12:24
 */
@Getter
@Setter
@Slf4j
public class XxlJobCookie {

    private final static long TIMEOUT = 2;
    private final static TimeUnit UNIT = TimeUnit.HOURS;

    private final static Admin ADMIN = SpringUtil.getBean(Admin.class);
    private final static XxlJobClient XXL_JOB_CLIENT = SpringUtil.getBean(XxlJobClient.class);

    private String cookie; // 若 cookie 不对，或者没有 cookie，请求 xxl-job 会返回空串
    private long deadline; //有效期限
    private volatile static XxlJobCookie XXL_JOB_COOKIE = null;

    private XxlJobCookie() {
    }

    private boolean isExpired() {
        return !StringUtils.hasText(this.cookie) || System.currentTimeMillis() > this.deadline;
    }

    private static void setCookie() {
        if(Objects.isNull(XXL_JOB_COOKIE)) {
            XXL_JOB_COOKIE = new XxlJobCookie();
        }
        // 登录
        String cookie = Optional.ofNullable(XXL_JOB_CLIENT.loginDo(ADMIN.getUsername(), ADMIN.getPassword(), null)).flatMap(entity -> {
            return Optional.ofNullable(entity.getBody()).filter(returnT -> returnT.getCode() == ReturnT.SUCCESS_CODE).map(returnT -> {
                return HttpRequestUtil.convertCookie(entity.getHeaders().get(HttpHeaders.SET_COOKIE));
            });
        }).filter(StringUtils::hasText).orElseThrow(() -> new GlobalServiceException("get xxl-job cookie error!"));
        // 计算 cookie 的截止时间
        long deadline = System.currentTimeMillis() + UNIT.toMillis(TIMEOUT);
        log.info("xxljob cookie {} deadline {}", cookie, deadline);
        XXL_JOB_COOKIE.setCookie(cookie);
        XXL_JOB_COOKIE.setDeadline(deadline);
    }

    public static XxlJobCookie getXxlJobCookie() {
        if(Objects.isNull(XXL_JOB_COOKIE) || XXL_JOB_COOKIE.isExpired()) {
            synchronized (XxlJobCookie.class) {
                if(Objects.isNull(XXL_JOB_COOKIE) || XXL_JOB_COOKIE.isExpired()) {
                    setCookie();
                }
            }
        }
        return XXL_JOB_COOKIE;
    }

}
