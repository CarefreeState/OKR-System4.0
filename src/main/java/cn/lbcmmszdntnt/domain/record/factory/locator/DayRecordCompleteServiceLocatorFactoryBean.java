package cn.lbcmmszdntnt.domain.record.factory.locator;


import cn.lbcmmszdntnt.domain.record.factory.DayaRecordCompleteServiceFactory;
import cn.lbcmmszdntnt.locator.CustomServiceLocatorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 16:48
 */
@Component
public class DayRecordCompleteServiceLocatorFactoryBean extends CustomServiceLocatorFactoryBean {

    @Override
    protected void init() {
        super.setServiceLocatorInterface(DayaRecordCompleteServiceFactory.class);
        super.setServiceMappings(PROPERTIES.getDayRecordCompleteServiceMap());
    }
}
