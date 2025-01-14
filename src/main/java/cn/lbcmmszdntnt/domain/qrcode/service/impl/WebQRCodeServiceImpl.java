package cn.lbcmmszdntnt.domain.qrcode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.common.util.web.HttpUtil;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.domain.qrcode.model.dto.WebQRCode;
import cn.lbcmmszdntnt.domain.qrcode.service.QRCodeService;
import cn.lbcmmszdntnt.domain.qrcode.strategy.QRCodeProcessStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 2:46
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebQRCodeServiceImpl implements QRCodeService {

    private final FileMediaService fileMediaService;

    @Override
    public <T> String getQRCode(T params, String scene, Long activeLimit, QRCodeProcessStrategy strategy) {
        WebQRCode webQRCode = BeanUtil.copyProperties(params, WebQRCode.class);
        String url = HttpUtil.buildUrl(webQRCode.getPage(), Map.of("scene", List.of(scene)));
        Integer width = webQRCode.getWidth();
        log.info("生成二维码 -> {}  {}  {} ", url, width, width);
        byte[] codeBytes = MediaUtil.getUrlQRCodeBytes(url, width, width);
        codeBytes = strategy.process(codeBytes);
        return fileMediaService.uploadImage(QRCodeConstants.DEFAULT_ORIGINAL_NAME, codeBytes, activeLimit);
    }
}
