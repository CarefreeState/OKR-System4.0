package cn.bitterfree.api.wxtoken.util;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.common.util.media.FileResourceUtil;
import cn.bitterfree.api.common.util.media.MediaUtil;
import cn.bitterfree.api.wxtoken.config.WxAdmin;
import cn.bitterfree.api.wxtoken.feign.WxHttpClient;
import cn.bitterfree.api.wxtoken.model.dto.AccessTokenDTO;
import cn.bitterfree.api.wxtoken.model.dto.WxQRCode;
import cn.bitterfree.api.wxtoken.model.vo.AccessTokenVO;
import cn.bitterfree.api.wxtoken.model.vo.JsCode2SessionVO;
import cn.bitterfree.api.wxtoken.token.AccessToken;
import cn.hutool.extra.spring.SpringUtil;
import feign.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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
    private final static WxHttpClient WX_HTTP_CLIENT = SpringUtil.getBean(WxHttpClient.class);

    public static AccessTokenVO accessToken(AccessTokenDTO accessTokenDTO) {
        accessTokenDTO.setAppid(WX_ADMIN.getAppid());
        accessTokenDTO.setSecret(WX_ADMIN.getSecret());
        AccessTokenVO accessTokenVO = WX_HTTP_CLIENT.stableToken(accessTokenDTO);
        log.info("wx token {}", accessTokenDTO);
        return accessTokenVO;
    }

    public static JsCode2SessionVO jsCode2Session(String jsCode) {
        return WX_HTTP_CLIENT.jscode2session(WX_ADMIN.getAppid(), WX_ADMIN.getSecret(), jsCode, "authorization_code");
    }

    public static byte[] wxQrcode(WxQRCode wxQRCode) {
        try (Response response = WX_HTTP_CLIENT.getwxacodeunlimit(AccessToken.getAccessToken().getToken(), wxQRCode)) {
            try (InputStream inputStream = response.body().asInputStream()) {
                byte[] data = MediaUtil.getBytes(inputStream);
                if(Objects.isNull(data) || !FileResourceUtil.isImage(MediaUtil.getContentType(data))) {
                    log.error("获取微信二维码失败 {}", Objects.isNull(data) ? null : new String(data));
                    throw new GlobalServiceException(GlobalServiceStatusCode.QR_CODE_GENERATE_FAIL);
                }
                return data;
            } catch (IOException e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }
    }

}
