package cn.bitterfree.api.domain.auth.service;

import cn.bitterfree.api.domain.qrcode.enums.QRCodeType;
import cn.bitterfree.api.domain.qrcode.model.vo.LoginQRCodeVO;
import cn.bitterfree.api.domain.user.model.entity.User;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 1:51
 */
public interface LoginAckIdentifyService {

    String getSecret();

    LoginQRCodeVO getLoginQRCode(String secret, QRCodeType codeType);

    void ackSecret(String secret, Long userId);

    User validateSecret(String secret);

}
