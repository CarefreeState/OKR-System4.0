package cn.lbcmmszdntnt.domain.core.factory.locator;


import cn.lbcmmszdntnt.domain.core.factory.TaskServiceFactory;
import cn.lbcmmszdntnt.locator.CustomServiceLocatorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 16:40
 */
@Component
public class TaskServiceLocatorFactoryBean extends CustomServiceLocatorFactoryBean {

    @Override
    protected void init() {
        super.setServiceLocatorInterface(TaskServiceFactory.class);
        super.setServiceMappings(PROPERTIES.getTaskServiceMap());
    }
}
