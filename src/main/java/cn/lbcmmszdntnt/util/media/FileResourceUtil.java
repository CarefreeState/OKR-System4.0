package cn.lbcmmszdntnt.util.media;

import cn.lbcmmszdntnt.common.enums.FileResourceType;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import org.springframework.util.StringUtils;

import static cn.lbcmmszdntnt.common.enums.FileResourceType.*;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-23
 * Time: 12:31
 */
public class FileResourceUtil {

    public static void checkOriginalName(String originalName) {
        // todo 目前不允许无后缀文件，若要上传，需要放在压缩文件里
        // 判断是否有非空字符以及是否有后缀
        if (!StringUtils.hasText(originalName) || !originalName.contains(".") || originalName.lastIndexOf(".") + 1 == originalName.length()) {
            throw new GlobalServiceException(String.format("资源名非法 %s", originalName), GlobalServiceStatusCode.FILE_RESOURCE_NOT_VALID);
        }
    }

    public static void checkSuffix(String suffix) {
        if (!StringUtils.hasText(suffix)|| suffix.length() <= 1 || !suffix.contains(".")) {
            throw new GlobalServiceException(String.format("后缀非法 %s", suffix), GlobalServiceStatusCode.FILE_RESOURCE_NOT_VALID);
        }
    }

    public static void checkExtension(String extension) {
        if (!StringUtils.hasText(extension)) {
            throw new GlobalServiceException(String.format("扩展名非法 %s", extension), GlobalServiceStatusCode.FILE_RESOURCE_NOT_VALID);
        }
    }

    public static boolean matchType(String contentType, FileResourceType fileResourceType) {
        return contentType.startsWith(fileResourceType.getContentTypeSuffix());
    }

    public static void checkType(String contentType, FileResourceType fileResourceType) {
        if(!matchType(contentType, fileResourceType)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.FILE_RESOURCE_TYPE_NOT_MATCH);
        }
    }

    public static boolean isImage(String contentType) {
        return matchType(contentType, IMAGE);
    }

    public static void checkImage(String contentType) {
        checkType(contentType, IMAGE);
    }

    public static boolean isVideo(String contentType) {
        return matchType(contentType, VIDEO);
    }

    public static void checkVideo(String contentType) {
        checkType(contentType, VIDEO);
    }

    public static boolean isText(String contentType) {
        return matchType(contentType, TEXT);
    }

    public static void checkText(String contentType) {
        checkType(contentType, TEXT);
    }


}
