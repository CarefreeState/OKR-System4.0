package cn.lbcmmszdntnt.wxtoken.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.media.FileResourceUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.common.util.web.HttpUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.wxtoken.AccessToken;
import cn.lbcmmszdntnt.wxtoken.enums.WxHttpRequest;
import cn.lbcmmszdntnt.wxtoken.model.dto.AccessTokenDTO;
import cn.lbcmmszdntnt.wxtoken.model.dto.JsCodeSessionDTO;
import cn.lbcmmszdntnt.wxtoken.model.dto.WxQRCode;
import cn.lbcmmszdntnt.wxtoken.model.vo.AccessTokenVO;
import cn.lbcmmszdntnt.wxtoken.model.vo.JsCode2SessionVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 3:32
 */
@Slf4j
public class WxHttpRequestUtil {

    private final static String APP_ID = SpringUtil.getProperty("wx.appid");

    private final static String APP_SECRET = SpringUtil.getProperty("wx.secret");

    public static AccessTokenVO accessToken(AccessTokenDTO accessTokenDTO) {
        accessTokenDTO.setAppid(APP_ID);
        accessTokenDTO.setSecret(APP_SECRET);
        WxHttpRequest accessToken = WxHttpRequest.ACCESS_TOKEN;
        return HttpUtil.jsonRequest(
                accessToken.getUrl(),
                accessToken.getMethod(),
                accessTokenDTO,
                AccessTokenVO.class,
                null
        );
    }

    public static JsCode2SessionVO jsCode2Session(JsCodeSessionDTO jsCodeSessionDTO) {
        WxHttpRequest jsCode2Session = WxHttpRequest.JS_CODE2_SESSION;
        String url = HttpUtil.buildUrl(jsCode2Session.getUrl(), Map.of(
                "appid", List.of(APP_ID),
                "secret", List.of(APP_SECRET),
                "js_code", List.of(jsCodeSessionDTO.getJsCode()),
                "grant_type", List.of(jsCodeSessionDTO.getGrantType())
        ));
        return HttpUtil.jsonRequest(url, jsCode2Session.getMethod(), null, JsCode2SessionVO.class, null);
    }

    public static byte[] wxQrcode(WxQRCode wxQRCode) {
        WxHttpRequest wxQrcode = WxHttpRequest.WX_QRCODE;
        String url = HttpUtil.buildUrl(wxQrcode.getUrl(), Map.of(
                "access_token", List.of(AccessToken.getAccessToken().getToken()))
        );
        byte[] data = HttpUtil.jsonRequest(url, wxQrcode.getMethod(), wxQRCode, null);
        if(!FileResourceUtil.isImage(MediaUtil.getContentType(data))) {
            log.error("获取微信二维码失败 {}", new String(data));
            throw new GlobalServiceException(GlobalServiceStatusCode.QR_CODE_GENERATE_FAIL);
        }
        return data;
    }

}
