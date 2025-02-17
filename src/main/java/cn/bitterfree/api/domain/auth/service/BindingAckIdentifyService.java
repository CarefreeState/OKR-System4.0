package cn.bitterfree.api.domain.auth.service;

import cn.bitterfree.api.domain.qrcode.model.vo.BindingQRCodeVO;
import cn.bitterfree.api.wxtoken.model.vo.JsCode2SessionVO;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 23:13
 */
public interface BindingAckIdentifyService {

    String getSecret();

    BindingQRCodeVO getBindingQRCode(String secret);

    void ackSecret(String secret, String code);

    JsCode2SessionVO validateSecret(String secret);

}
