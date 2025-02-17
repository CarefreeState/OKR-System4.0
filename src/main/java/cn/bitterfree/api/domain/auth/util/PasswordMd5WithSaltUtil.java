package cn.bitterfree.api.domain.auth.util;

import cn.bitterfree.api.common.util.convert.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-06
 * Time: 0:41
 */
@Slf4j
public class PasswordMd5WithSaltUtil {

    private final static String PASSWORD_SEPARATOR = "$";

    // 获取盐值
    public static String getSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static String assemble(String salt, String password) {
        return salt + PASSWORD_SEPARATOR + encrypt(salt, password);
    }

    // 加盐加密
    public static String encrypt(String salt, String password) {
        return EncryptUtil.md5(salt + password);
    }

    // 验证密码
    public static boolean confirm(String salt, String inputPassword, String encryptPassword) {
        return StringUtils.hasText(salt) &&
                StringUtils.hasText(inputPassword) &&
                StringUtils.hasText(encryptPassword) &&
                encryptPassword.equals(EncryptUtil.md5(salt + inputPassword));
    }

    public static String encrypt(String password) {
        return assemble(getSalt(), password);
    }

    public static boolean confirm(String inputPassword, String dbPassword) {
        try {
            int separatorIndex = dbPassword.indexOf(PASSWORD_SEPARATOR);
            String salt = dbPassword.substring(0, separatorIndex);
            String encryptPassword = dbPassword.substring(separatorIndex + 1);
            boolean confirm = confirm(salt, inputPassword, encryptPassword);
            log.info("inputPassword {}, dbPassword {} (salt {}, encryptPassword {}) -> {}",
                    inputPassword, dbPassword, salt, encryptPassword, confirm);
            return confirm;
        } catch (Exception e) {
            log.info("inputPassword {}, dbPassword {} -> false ({})", inputPassword, dbPassword, e.getMessage());
            return Boolean.FALSE;
        }
    }
}
