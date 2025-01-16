package cn.lbcmmszdntnt.wxtoken.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.media.FileResourceUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.common.util.web.HttpRequestUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.wxtoken.config.WxAdmin;
import cn.lbcmmszdntnt.wxtoken.enums.WxHttpRequest;
import cn.lbcmmszdntnt.wxtoken.model.dto.AccessTokenDTO;
import cn.lbcmmszdntnt.wxtoken.model.dto.JsCode2SessionDTO;
import cn.lbcmmszdntnt.wxtoken.model.dto.WxQRCode;
import cn.lbcmmszdntnt.wxtoken.model.vo.AccessTokenVO;
import cn.lbcmmszdntnt.wxtoken.model.vo.JsCode2SessionVO;
import cn.lbcmmszdntnt.wxtoken.token.AccessToken;
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

    private final static WxAdmin WX_ADMIN = SpringUtil.getBean(WxAdmin.class);

    public static AccessTokenVO accessToken(AccessTokenDTO accessTokenDTO) {
        accessTokenDTO.setAppid(WX_ADMIN.getAppid());
        accessTokenDTO.setSecret(WX_ADMIN.getSecret());
        WxHttpRequest accessToken = WxHttpRequest.ACCESS_TOKEN;
        return HttpRequestUtil.jsonRequest(
                accessToken.getUrl(),
                accessToken.getMethod(),
                accessTokenDTO,
                AccessTokenVO.class,
                null
        );
    }

    public static JsCode2SessionVO jsCode2Session(JsCode2SessionDTO jsCode2SessionDTO) {
        WxHttpRequest jsCode2Session = WxHttpRequest.JS_CODE2_SESSION;
        String url = HttpRequestUtil.buildUrl(jsCode2Session.getUrl(), Map.of(
                "appid", List.of(WX_ADMIN.getAppid()),
                "secret", List.of(WX_ADMIN.getSecret()),
                "js_code", List.of(jsCode2SessionDTO.getJsCode()),
                "grant_type", List.of(jsCode2SessionDTO.getGrantType())
        ));
        return HttpRequestUtil.jsonRequest(url, jsCode2Session.getMethod(), null, JsCode2SessionVO.class, null);
    }

    public static byte[] wxQrcode(WxQRCode wxQRCode) {
        WxHttpRequest wxQrcode = WxHttpRequest.WX_QRCODE;
        String url = HttpRequestUtil.buildUrl(wxQrcode.getUrl(), Map.of(
                "access_token", List.of(AccessToken.getAccessToken().getToken()))
        );
        try(HttpResponse execute = HttpRequestUtil.jsonRequest(url, wxQrcode.getMethod(), wxQRCode, null)) {
            byte[] data = execute.bodyBytes();
            if(!FileResourceUtil.isImage(MediaUtil.getContentType(data))) {
                log.error("获取微信二维码失败 {}", new String(data));
                throw new GlobalServiceException(GlobalServiceStatusCode.QR_CODE_GENERATE_FAIL);
            }
            return data;
        }
    }

}
