package cn.bitterfree.api.wxtoken.token;

import cn.bitterfree.api.wxtoken.model.dto.AccessTokenDTO;
import cn.bitterfree.api.wxtoken.model.vo.AccessTokenVO;
import cn.bitterfree.api.wxtoken.util.WxHttpRequestUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class AccessToken {

    private final static TimeUnit UNIT = TimeUnit.SECONDS;

    private String token;
    private long deadline;
    private volatile static AccessToken ACCESS_TOKEN = null;

    private AccessToken() {
    }

    public boolean isExpired() {
        return !StringUtils.hasText(this.token) || System.currentTimeMillis() > this.deadline;
    }

    private static void setAccessToken() {
        if(Objects.isNull(ACCESS_TOKEN)) {
            ACCESS_TOKEN = new AccessToken();
        }
        AccessTokenVO accessTokenVO = WxHttpRequestUtil.accessToken(AccessTokenDTO.builder().build());
        ACCESS_TOKEN.setToken(accessTokenVO.getAccessToken());
        ACCESS_TOKEN.setDeadline(System.currentTimeMillis() + UNIT.toMillis(accessTokenVO.getExpiresIn()));
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
