package cn.lbcmmszdntnt.domain.okr.init;


import com.github.lalyos.jfiglet.FigletFont;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-13
 * Time: 23:46
 */
@Component
@Order(0)
public class WelcomeInitializer implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${spring.application.name}")
    private String name;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println(FigletFont.convertOneLine(name));
    }
}
