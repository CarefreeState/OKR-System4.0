package cn.bitterfree.api.common.util.convert;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 16:11
 */
public class EncryptUtil {

    private final static Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private final static Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    public static String encodeBase64(String originSrr) {
        return new String(BASE64_ENCODER.encode(originSrr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }


    public static String decodeBase64(String base64Str) {
        return new String(BASE64_DECODER.decode(base64Str), StandardCharsets.UTF_8);
    }

    // md5加密
    public static String md5(String normal) {
        return DigestUtils.md5Hex(normal);
    }

    public static String sha1(String... strings) {
        StringBuilder builder = new StringBuilder();
        for(String s : strings) {
            builder.append(s);
        }
        // 加密
        return DigestUtils.sha1Hex(builder.toString());
    }
}
