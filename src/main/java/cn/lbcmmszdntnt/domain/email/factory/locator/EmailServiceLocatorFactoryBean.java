package cn.lbcmmszdntnt.domain.email.factory.locator;

import cn.lbcmmszdntnt.domain.email.factory.EmailServiceFactory;
import cn.lbcmmszdntnt.locator.CustomServiceLocatorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 16:15
 */
@Component
public class EmailServiceLocatorFactoryBean extends CustomServiceLocatorFactoryBean {

    @Override
    protected void init() {
        super.setServiceLocatorInterface(EmailServiceFactory.class);
        super.setServiceMappings(PROPERTIES.getEmailServiceMap());
    }
}
