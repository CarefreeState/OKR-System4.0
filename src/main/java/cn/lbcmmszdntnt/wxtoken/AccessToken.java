package cn.lbcmmszdntnt.wxtoken;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class AccessToken {

    private String token;

    private long expireIn;//有效期限

    volatile private static AccessToken ACCESS_TOKEN = null;

    private AccessToken() {

    }

    private void setExpireIn(int expireIn) {
        // 设置有效期限的时候的时间戳
        this.expireIn = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expireIn);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.getExpireIn();
    }

    private static void setAccessToken() {
        if(ACCESS_TOKEN == null) {
            ACCESS_TOKEN = new AccessToken();
        }
        Map<String, Object> map = TokenUtil.getAccessTokenMap();
        ACCESS_TOKEN.setToken((String) map.get("access_token"));
        ACCESS_TOKEN.setExpireIn((Integer) map.get("expires_in"));
    }

    public static AccessToken getAccessToken() {
        if(ACCESS_TOKEN == null || ACCESS_TOKEN.isExpired()) {
            synchronized (AccessToken.class) {
                if(ACCESS_TOKEN == null || ACCESS_TOKEN.isExpired()) {
                    setAccessToken();
                }
            }
        }
        return ACCESS_TOKEN;
    }
}
