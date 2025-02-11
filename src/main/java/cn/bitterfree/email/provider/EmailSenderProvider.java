package cn.bitterfree.email.provider;

import cn.bitterfree.email.provider.strategy.ProvideStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-13
 * Time: 22:50
 */
@Component
@RequiredArgsConstructor
public class EmailSenderProvider {

    private final ProvideStrategy provideStrategy;

    private final List<JavaMailSenderImpl> javaMailSenderList;

    public JavaMailSenderImpl provide() {
        return provideStrategy.getSender(javaMailSenderList);
    }

}
