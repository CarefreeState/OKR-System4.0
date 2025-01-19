package cn.lbcmmszdntnt.common.util.convert;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;

public class ShortCodeUtil {

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    public static final int FETCH_RADIX = 16;
    public static final int MODULES = CHARSET.length();
    public static final int FETCH_SIZE = 4;

    public static String subCodeByString(String str, int length) {
        str = EncryptUtil.md5(str);
        int strLength = str.length();
        int gap = strLength / length;//取值间隔
        if(gap < 1 || (length - 1) * gap + FETCH_SIZE > strLength) {
            String message = String.format("哈希字符串 %s，无法取出 %d 个 %d 位 %d 进制数", str, length, FETCH_SIZE, FETCH_RADIX);
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_NOT_VALID);
        }
        StringBuilder subCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = Integer.parseInt(str.substring(i * gap, i * gap + FETCH_SIZE), FETCH_RADIX);//提取十六进制数
            subCode.append(CHARSET.charAt(index % MODULES));//对应到Base64字典的某个Base64字符
        }
        return subCode.toString();
    }

}
