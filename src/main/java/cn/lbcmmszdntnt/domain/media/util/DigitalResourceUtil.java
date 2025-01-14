package cn.lbcmmszdntnt.domain.media.util;

import java.util.UUID;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 0:54
 */
public class DigitalResourceUtil {

    public static String getCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
