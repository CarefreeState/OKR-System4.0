package cn.lbcmmszdntnt.domain.qrcode.provider;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 15:14
 */
public interface QRCodeProvider {

    interface QRCodeProcessor {
        byte[] process(byte[] bytes);
    }

    <T> String getQRCode(T params, String scene, Long activeLimit, QRCodeProcessor strategy);

    String getInviteQRCode(Long teamId, String teamName, String secret);
    String getCommonQRCode(); // 访问域名查看主页二维码，再扫码去主页，还不如直接代理域名到主页
    String getLoginQRCode(String secret);
    String getBindingQRCode(String secret); // 绑定码只用于绑定微信

}
