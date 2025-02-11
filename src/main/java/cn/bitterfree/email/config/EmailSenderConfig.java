package cn.bitterfree.email.config;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.common.util.convert.ObjectUtil;
import cn.bitterfree.email.provider.strategy.ProvideStrategy;
import cn.bitterfree.email.provider.strategy.RoundRobinProvideStrategy;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-10-13
 * Time: 22:14
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("okr.mail")
public class EmailSenderConfig {

    private List<EmailSenderProperties> senders;

    @Data
    public static class EmailSenderProperties {

        private String username;

        private String password;

        private String host;

        private Integer port;

        private String protocol;

        private String defaultEncoding;

        private Properties properties;

    }

    @Bean
    @ConditionalOnMissingBean(ProvideStrategy.class)
    public ProvideStrategy provideStrategy() {
        // 默认是轮询
        return new RoundRobinProvideStrategy();
    }

    @Bean
    public List<JavaMailSenderImpl> javaMailSenderList() {
        // 构造邮件发送器实现
        List<JavaMailSenderImpl> senderList = new ArrayList<>();
        ObjectUtil.nonNullstream(this.senders).forEach(sender -> {
            // 邮件发送者
            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setHost(sender.getHost());
            javaMailSender.setPort(sender.getPort());
            javaMailSender.setUsername(sender.getUsername());
            javaMailSender.setPassword(sender.getPassword());
            javaMailSender.setProtocol(sender.getProtocol());
            javaMailSender.setDefaultEncoding(sender.getDefaultEncoding());
            javaMailSender.setJavaMailProperties(sender.getProperties());
            senderList.add(javaMailSender);
        });
        // 若不存在一个实现则抛出异常（启动项目时）
        if(CollectionUtils.isEmpty(senderList)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.EMAIL_SENDER_NOT_EXISTS);
        }
        return senderList;
    }

}
