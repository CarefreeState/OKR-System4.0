package cn.bitterfree.api.common.util.convert;

import java.util.UUID;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-15
 * Time: 18:34
 */
public class UUIDUtil {

    public static String uuid32() {
        return uuid36().replace("-", "");
    }

    public static String uuid36() {
        return UUID.randomUUID().toString();
    }

}
