package cn.lbcmmszdntnt.xxljob.cookie;

import cn.lbcmmszdntnt.xxljob.util.XxlJobRequestUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
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
public class XxlJobCookie {

    private final static long TIMEOUT = 2;

    private final static TimeUnit UNIT = TimeUnit.HOURS;

    private String cookie;

    private long expireIn;//有效期限

    private volatile static XxlJobCookie XXL_JOB_COOKIE = null;

    private XxlJobCookie() {
    }

    private static void setCookie() {
        if(Objects.isNull(XXL_JOB_COOKIE)) {
            XXL_JOB_COOKIE = new XxlJobCookie();
        }
        XXL_JOB_COOKIE.setCookie(XxlJobRequestUtil.login());
        XXL_JOB_COOKIE.setExpireIn(System.currentTimeMillis() + UNIT.toMillis(TIMEOUT));
    }

    private boolean isExpired() {
       return Objects.isNull(this.cookie) || System.currentTimeMillis() > this.expireIn;
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
