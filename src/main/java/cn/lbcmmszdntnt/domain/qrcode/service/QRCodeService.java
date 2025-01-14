package cn.lbcmmszdntnt.domain.qrcode.service;

import cn.lbcmmszdntnt.domain.qrcode.strategy.QRCodeProcessStrategy;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 2:34
 */
public interface QRCodeService {

    <T> String getQRCode(T params, String scene, Long activeLimit, QRCodeProcessStrategy strategy);

}
