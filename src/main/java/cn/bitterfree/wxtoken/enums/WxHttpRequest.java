package cn.bitterfree.wxtoken.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 3:10
 */
@Getter
@AllArgsConstructor
public enum WxHttpRequest {

    ACCESS_TOKEN("https://api.weixin.qq.com/cgi-bin/stable_token", "POST"),
    JS_CODE2_SESSION("https://api.weixin.qq.com/sns/jscode2session", "GET"),
    WX_QRCODE("https://api.weixin.qq.com/wxa/getwxacodeunlimit", "POST"),
    ;

    private final String url;

    private final String method;
}
