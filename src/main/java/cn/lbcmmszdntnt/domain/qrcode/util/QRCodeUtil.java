package cn.lbcmmszdntnt.domain.qrcode.util;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.media.FileResourceUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.common.util.web.HttpUtil;
import cn.lbcmmszdntnt.domain.qrcode.config.QRCodeConfig;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.wxtoken.TokenUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-22
 * Time: 21:23
 */
@Slf4j
public class QRCodeUtil {

    public static byte[] doPostGetQRCodeData(String json) {
        String accessToken = TokenUtil.getToken();
        String url = HttpUtil.buildUrl(QRCodeConfig.WX_QR_CORE_URL, Map.of("access_token", List.of(accessToken)));
        log.info("请求微信（json） -> {}", json);
        byte[] data = HttpUtil.doPostJsonBytes(url, json);
        if(!FileResourceUtil.isImage(MediaUtil.getContentType(data))) {
            throw new GlobalServiceException(new String(data), GlobalServiceStatusCode.QR_CODE_GENERATE_FAIL);
        }
        // 保存一下
        return data;
    }

}
