package cn.lbcmmszdntnt.domain.qrcode.service.impl;

import cn.lbcmmszdntnt.config.WebMvcConfiguration;
import cn.lbcmmszdntnt.domain.qrcode.config.properties.WxCommonQRCode;
import cn.lbcmmszdntnt.domain.qrcode.service.WxCommonQRCodeService;
import cn.lbcmmszdntnt.domain.qrcode.util.QRCodeUtil;
import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-04
 * Time: 1:05
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WxCommonQRCodeServiceImpl implements WxCommonQRCodeService {

    private final static String SCENE = "you=nice";

    private final WxCommonQRCode wxCommonQRCode;

    @Override
    public Color getQRCodeColor() {
        return wxCommonQRCode.getQrCodeColor();
    }

    @Override
    public String getQRCode() {
        Map<String, Object> params = wxCommonQRCode.getQRCodeParams();
        params.put("scene", SCENE);
        String json = JsonUtil.analyzeData(params);
        return MediaUtil.saveImage(QRCodeUtil.doPostGetQRCodeData(json), WebMvcConfiguration.COMMON_PATH);
    }
}
