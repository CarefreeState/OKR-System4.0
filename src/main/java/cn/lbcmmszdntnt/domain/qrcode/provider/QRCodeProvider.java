package cn.lbcmmszdntnt.domain.qrcode.provider;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 15:14
 */
public interface QRCodeProvider {

    String getInviteQRCode(Long teamId, String teamName, String secret);
    String getCommonQRCode();
    String getLoginQRCode(String secret);
    String getBindingQRCode(Long userId, String secret);

}
