package cn.bitterfree.domain.teaminvite.service;

import cn.bitterfree.domain.qrcode.enums.QRCodeType;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 17:06
 */
public interface TeamInviteIdentifyService {

    String getInviteQRCode(Long teamId, QRCodeType qrCodeType);

    void validateSecret(Long userId, Long teamId, String secret);

}
