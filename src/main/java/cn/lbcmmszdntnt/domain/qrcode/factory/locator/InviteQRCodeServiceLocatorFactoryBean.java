package cn.lbcmmszdntnt.domain.qrcode.factory.locator;

import cn.lbcmmszdntnt.domain.qrcode.factory.InviteQRCodeServiceFactory;
import cn.lbcmmszdntnt.locator.CustomServiceLocatorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 15:23
 */
@Component
public class InviteQRCodeServiceLocatorFactoryBean extends CustomServiceLocatorFactoryBean {

    @Override
    protected void init() {
        super.setServiceLocatorInterface(InviteQRCodeServiceFactory.class);
        super.setServiceMappings(PROPERTIES.getInviteQRCodeServiceMap());
    }
}