package cn.lbcmmszdntnt.domain.auth.factory;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.domain.auth.enums.LoginType;
import cn.lbcmmszdntnt.domain.login.service.LoginService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 10:41
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "okr.service.login-service")
public class LoginServiceFactory {

    private Map<LoginType, String> map;

    public LoginService getService(LoginType loginType) {
        return SpringUtil.getBean(map.get(loginType), LoginService.class);
    }

}
