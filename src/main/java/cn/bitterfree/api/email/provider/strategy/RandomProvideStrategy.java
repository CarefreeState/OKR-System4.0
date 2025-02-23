package cn.bitterfree.api.email.provider.strategy;

import cn.bitterfree.api.common.util.convert.ObjectUtil;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-13
 * Time: 22:21
 */
public class RandomProvideStrategy implements ProvideStrategy {

    @Override
    public JavaMailSenderImpl getSender(List<JavaMailSenderImpl> senderList) {
        return ObjectUtil.randomOne(senderList);
    }
}
