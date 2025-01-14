package cn.lbcmmszdntnt.domain.qrcode.strategy;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 14:09
 */
public interface QRCodeProcessStrategy {

    byte[] process(byte[] bytes);

}
