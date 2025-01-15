package cn.lbcmmszdntnt.email.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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

    private String strategy;

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

}
