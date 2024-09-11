package cn.lbcmmszdntnt.domain.user.factory.locator;

import cn.lbcmmszdntnt.domain.user.factory.LoginServiceFactory;
import cn.lbcmmszdntnt.locator.CustomServiceLocatorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 11:09
 */
@Component
public class LoginServiceLocatorFactoryBean extends CustomServiceLocatorFactoryBean {

    @Override
    protected void init() {
        super.setServiceLocatorInterface(LoginServiceFactory.class);
        super.setServiceMappings(PROPERTIES.getLoginServiceMap());
    }

}
