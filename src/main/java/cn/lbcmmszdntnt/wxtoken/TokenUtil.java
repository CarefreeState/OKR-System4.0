package cn.lbcmmszdntnt.wxtoken;


import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.common.util.web.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TokenUtil {

    public static String APP_ID;

    public static String APP_SECRET;

    @Value("${wx.appid}")
    private void setAPP_ID(String appId) {
        APP_ID = appId;
    }

    @Value("${wx.secret}")
    private void setAPP_SECRET(String appSecret) {
        APP_SECRET = appSecret;
    }


    public static Map<String, Object> getAccessTokenMap() {
        // 构造参数表
        String json = JsonUtil.jsonBuilder()
                .put("grant_type", "client_credential")
                .put("appid", APP_ID)
                .put("secret", APP_SECRET)
                .build();
        // 发起get请求
        String response = HttpUtil.doPostJsonString("https://api.weixin.qq.com/cgi-bin/stable_token", json);
        // 解析json
        return JsonUtil.analyzeJson(response, Map.class);
    }

    public static String getToken() {
        return AccessToken.getAccessToken().getToken();
    }
}
