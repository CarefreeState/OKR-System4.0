package cn.lbcmmszdntnt.domain.qrcode.service.impl;

import cn.lbcmmszdntnt.config.WebMvcConfiguration;
import cn.lbcmmszdntnt.domain.qrcode.config.properties.WxLoginQRCode;
import cn.lbcmmszdntnt.domain.qrcode.service.WxLoginQRCodeService;
import cn.lbcmmszdntnt.domain.qrcode.util.QRCodeUtil;
import cn.lbcmmszdntnt.util.convert.JsonUtil;
import cn.lbcmmszdntnt.util.media.MediaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-20
 * Time: 22:34
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WxLoginQRCodeServiceImpl implements WxLoginQRCodeService {

    private final WxLoginQRCode wxLoginQRCode;

    @Override
    public Color getQRCodeColor() {
        return wxLoginQRCode.getQrCodeColor();
    }

    @Override
    public String getQRCode(String secret) {
        Map<String, Object> params = wxLoginQRCode.getQRCodeParams();
        String scene = String.format("%s=%s", wxLoginQRCode.getSecret(), secret);
        params.put("scene", scene);
        String json = JsonUtil.analyzeData(params);
        return MediaUtil.saveImage(QRCodeUtil.doPostGetQRCodeData(json), WebMvcConfiguration.LOGIN_PATH);
    }

}
