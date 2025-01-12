package cn.lbcmmszdntnt.config;

import cn.lbcmmszdntnt.domain.media.service.ObjectStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-23
 * Time: 10:49
 */
@Configuration
public class ObjectStorageServiceConfig {

    @Bean
    public ObjectStorageService objectStorageService() {
        // 获取实例的时候，并不是 bean，可以理解为只执行了初始化方法（生命周期的第一步），所以没有依赖注入之类的，在这里 return 可以根据类的定义继续完成创建 bean 的过程
        return SpringFactoriesLoader.loadFactories(ObjectStorageService.class, this.getClass().getClassLoader()).getFirst();
        // 此对象通过 spi 加载后通过 @Bean 放入 Spring 容器，已经构造过了，所以不能通过构造方法注入，只能属性注入！
//        return ServiceLoader.load(ObjectStorageService.class).findFirst().orElse(null);
    }

}
