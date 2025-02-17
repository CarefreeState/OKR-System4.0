package cn.bitterfree.api.domain.qrcode.service;

import cn.bitterfree.api.domain.qrcode.enums.QRCodeType;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-10
 * Time: 21:05
 */
public interface QRCodeService {

    String getInviteQRCode(Long teamId, String teamName, String secret, QRCodeType type);
    void deleteTeamNameQRCodeCache(Long teamId);

    String getBindingQRCode(String secret);
    String getLoginQRCode(String secret, QRCodeType type);
    String getCommonQRCode();

}
