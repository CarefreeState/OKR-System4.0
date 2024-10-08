package cn.lbcmmszdntnt.domain.qrcode.service;

import java.awt.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 19:36
 */
public interface WxBindingQRCodeService {

    Color getQRCodeColor();

    String getQRCode(Long userId, String randomCode);

    void checkParams(Long userId, String randomCode);

}
