package cn.lbcmmszdntnt.domain.qrcode.service;

import cn.lbcmmszdntnt.domain.qrcode.enums.QRCodeType;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.BindingQRCodeVO;
import cn.lbcmmszdntnt.domain.qrcode.model.vo.LoginQRCodeVO;

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

    BindingQRCodeVO getBindingQRCode();

    LoginQRCodeVO getLoginQRCode(QRCodeType type);

    String getCommonQRCode();

}
