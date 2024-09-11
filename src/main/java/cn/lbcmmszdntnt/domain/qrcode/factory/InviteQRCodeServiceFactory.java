package cn.lbcmmszdntnt.domain.qrcode.factory;

import cn.lbcmmszdntnt.domain.qrcode.service.InviteQRCodeService;
import cn.lbcmmszdntnt.locator.ServiceFactory;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 15:22
 */
public interface InviteQRCodeServiceFactory extends ServiceFactory<String, InviteQRCodeService> {

    String WX_TYPE = "wx";

    String WEB_TYPE = "web";

}
