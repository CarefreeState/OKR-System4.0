package cn.lbcmmszdntnt.domain.qrcode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.qrcode.service.QRCodeService;
import cn.lbcmmszdntnt.domain.qrcode.strategy.QRCodeProcessStrategy;
import cn.lbcmmszdntnt.wxtoken.model.dto.WxQRCode;
import cn.lbcmmszdntnt.wxtoken.util.WxHttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 2:37
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WxQRCodeServiceImpl implements QRCodeService {

    private final FileMediaService fileMediaService;

    @Override
    public <T> String getQRCode(T params, String scene, Long activeLimit, QRCodeProcessStrategy strategy) {
        WxQRCode wxQRCode = BeanUtil.copyProperties(params, WxQRCode.class);
        wxQRCode.setScene(scene);
        byte[] bytes = WxHttpRequestUtil.wxQrcode(wxQRCode);
        bytes = strategy.process(bytes);
        return fileMediaService.uploadImage(QRCodeConstants.DEFAULT_ORIGINAL_NAME, bytes, activeLimit);
    }
}
