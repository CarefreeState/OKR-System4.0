package cn.lbcmmszdntnt.domain.auth.service;

import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.LoginQRCodeVO;
import cn.lbcmmszdntnt.domain.user.model.entity.User;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 1:51
 */
public interface LoginAckIdentifyService {

    LoginQRCodeVO getLoginQRCode(QRCodeType codeType);

    void ackSecret(String secret, Long userId);

    User validateSecret(String secret);

}
