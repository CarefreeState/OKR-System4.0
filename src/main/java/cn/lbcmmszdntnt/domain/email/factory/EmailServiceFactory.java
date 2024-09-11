package cn.lbcmmszdntnt.domain.email.factory;

import cn.lbcmmszdntnt.domain.email.service.EmailService;
import cn.lbcmmszdntnt.locator.ServiceFactory;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 16:14
 */
public interface EmailServiceFactory extends ServiceFactory<String, EmailService> {

    String EMAIL_LOGIN = "email-login";

    String EMAIL_BINDING = "email-binding";
}
