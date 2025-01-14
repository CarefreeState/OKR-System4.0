package cn.lbcmmszdntnt.wxtoken;

import cn.lbcmmszdntnt.wxtoken.model.dto.AccessTokenDTO;
import cn.lbcmmszdntnt.wxtoken.model.vo.AccessTokenVO;
import cn.lbcmmszdntnt.wxtoken.util.WxHttpRequestUtil;
import lombok.Getter;
import lombok.Setter;

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
        AccessTokenVO accessTokenVO = WxHttpRequestUtil.accessToken(AccessTokenDTO.builder().build());
        ACCESS_TOKEN.setToken(accessTokenVO.getAccessToken());
        ACCESS_TOKEN.setExpireIn(accessTokenVO.getExpiresIn());
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
