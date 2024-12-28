package cn.lbcmmszdntnt.domain.media.service.impl;

import cn.lbcmmszdntnt.common.enums.FileResourceType;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.media.service.ObjectStorageService;
import cn.lbcmmszdntnt.util.media.FileResourceUtil;
import cn.lbcmmszdntnt.util.media.MediaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-16
 * Time: 13:07
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileMediaServiceImpl implements FileMediaService {

    @Value("${resource.compression.threshold}")
    private Integer compressionThreshold;

    private final ObjectStorageService objectStorageService;

    @Override
    public String uploadFile(MultipartFile file) {
        return uploadFile(FileResourceUtil.getOriginalName(file), MediaUtil.getBytes(file));
    }

    @Override
    public String uploadFile(String originalName, byte[] data) {
        // 判断文件类型
        String contentType = MediaUtil.getContentType(data);
        String suffix = null;
        // 判断是否是图片类型，并判断是否达到压缩阈值（否则压缩适得其反），若是则压缩图片
        if(FileResourceUtil.matchType(contentType, FileResourceType.IMAGE) && compressionThreshold.compareTo(data.length) <= 0) {
            // 压缩图片
            data = MediaUtil.compressImage(data);
            suffix = MediaUtil.COMPRESS_FORMAT_SUFFIX;
            originalName = FileResourceUtil.changeSuffix(originalName, suffix);
        } else {
            // 使用原后缀
            suffix = FileResourceUtil.getSuffix(originalName);
        }
        return objectStorageService.upload(originalName, data);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        return uploadImage(FileResourceUtil.getOriginalName(file), MediaUtil.getBytes(file));
    }

    @Override
    public String uploadImage(String originalName, byte[] data) {
        // 判断文件类型
        String contentType = MediaUtil.getContentType(data);
        String suffix = null;
        // 判断是否是图片类型
        FileResourceUtil.checkImage(contentType);
        // 判断是否压缩
        if(compressionThreshold.compareTo(data.length) <= 0) {
            // 压缩图片
            data = MediaUtil.compressImage(data);
            suffix = MediaUtil.COMPRESS_FORMAT_SUFFIX;
            originalName = FileResourceUtil.changeSuffix(originalName, suffix);
        } else {
            // 使用原后缀
            suffix = FileResourceUtil.getSuffix(originalName);
        }
        return objectStorageService.upload(originalName, data);
    }
}
