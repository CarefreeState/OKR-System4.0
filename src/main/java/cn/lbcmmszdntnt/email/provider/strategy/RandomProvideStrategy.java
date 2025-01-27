package cn.lbcmmszdntnt.email.provider.strategy;

import cn.hutool.core.util.RandomUtil;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-13
 * Time: 22:21
 */
@Component
public class RandomProvideStrategy implements ProvideStrategy {

    @Override
    public JavaMailSenderImpl getSender(List<JavaMailSenderImpl> senderList) {
        return senderList.get(RandomUtil.randomInt(senderList.size()));
    }
}
