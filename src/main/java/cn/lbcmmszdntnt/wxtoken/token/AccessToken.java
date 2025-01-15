package cn.lbcmmszdntnt.wxtoken.token;

import cn.lbcmmszdntnt.wxtoken.model.dto.AccessTokenDTO;
import cn.lbcmmszdntnt.wxtoken.model.vo.AccessTokenVO;
import cn.lbcmmszdntnt.wxtoken.util.WxHttpRequestUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class AccessToken {

    private final static TimeUnit UNIT = TimeUnit.SECONDS;

    private String token;

    private long expireIn;//有效期限

    private volatile static AccessToken ACCESS_TOKEN = null;

    private AccessToken() {
    }

    public boolean isExpired() {
        return Objects.isNull(this.token) || System.currentTimeMillis() > this.expireIn;
    }

    private static void setAccessToken() {
        if(Objects.isNull(ACCESS_TOKEN)) {
            ACCESS_TOKEN = new AccessToken();
        }
        AccessTokenVO accessTokenVO = WxHttpRequestUtil.accessToken(AccessTokenDTO.builder().build());
        ACCESS_TOKEN.setToken(accessTokenVO.getAccessToken());
        ACCESS_TOKEN.setExpireIn(System.currentTimeMillis() + UNIT.toMillis(accessTokenVO.getExpiresIn()));
    }

    public static AccessToken getAccessToken() {
        if(Objects.isNull(ACCESS_TOKEN) || ACCESS_TOKEN.isExpired()) {
            synchronized (AccessToken.class) {
                if(Objects.isNull(ACCESS_TOKEN) || ACCESS_TOKEN.isExpired()) {
                    setAccessToken();
                }
            }
        }
        return ACCESS_TOKEN;
    }
}
