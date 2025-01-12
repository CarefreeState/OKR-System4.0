package cn.lbcmmszdntnt.common.util.media;

import cn.lbcmmszdntnt.common.enums.FileResourceType;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.convert.DateTimeUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

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

    public static String getOriginalName(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        checkOriginalName(originalName);
        return originalName;
    }

    public static String getFileNameExcludeSuffix(String originalName) {
        checkOriginalName(originalName);
        return originalName.substring(0, originalName.lastIndexOf("."));
    }

    public static String getFileNameExcludeSuffix(MultipartFile file) {
        return getFileNameExcludeSuffix(file.getOriginalFilename());
    }

    public static String getSuffix(String originalName) {
        checkOriginalName(originalName);
        return originalName.substring(originalName.lastIndexOf("."));
    }

    public static String getExtension(String originalName) {
        return getSuffix(originalName).substring(1);
    }

    public static String changeSuffix(String originalName, String suffix) {
        checkSuffix(suffix);
        return getFileNameExcludeSuffix(originalName) + suffix;
    }

    public static String changeExtension(String originalName, String extension) {
        checkExtension(extension);
        return changeSuffix(originalName, "." + extension);
    }

    public static String getSimpleFileName(String suffix) {
        checkSuffix(suffix);
        return UUID.randomUUID().toString().replace("-", "") + suffix;
    }

    public static String getFileNameBySuffix(String filename, String suffix) {
        checkSuffix(suffix);
        return StringUtils.hasText(filename) ? filename + suffix : getSimpleFileName(suffix);
    }

    public static String getFileNameByExtension(String filename, String extension) {
        checkExtension(extension);
        return getFileNameBySuffix(filename, "." + extension);
    }

    public static String getUniqueFileName(String suffix) {
        return String.format(
                "%s/%s/%s",
                suffix,
                DateTimeUtil.getOnlyDateFormat(new Date()),
                getSimpleFileName(suffix)
        );
    }


}
