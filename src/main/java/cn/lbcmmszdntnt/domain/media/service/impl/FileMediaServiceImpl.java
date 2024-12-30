package cn.lbcmmszdntnt.domain.media.service.impl;

import cn.lbcmmszdntnt.common.enums.FileResourceType;
import cn.lbcmmszdntnt.domain.media.model.entity.DigitalResource;
import cn.lbcmmszdntnt.domain.media.service.DigitalResourceService;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.media.service.ObjectStorageService;
import cn.lbcmmszdntnt.common.util.media.FileResourceUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import cn.lbcmmszdntnt.redis.lock.RedisLockProperties;
import cn.lbcmmszdntnt.redis.lock.strategy.SimpleLockStrategy;
import jakarta.servlet.http.HttpServletResponse;
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

    private final DigitalResourceService digitalResourceService;

    private final ObjectStorageService objectStorageService;

    @Override
    public DigitalResource analyzeCode(String code) {
        return digitalResourceService.getResourceByCode(code);
    }

    @Override
    public void preview(String code, HttpServletResponse response) {
        DigitalResource resource = analyzeCode(code);
        objectStorageService.preview(resource.getFileName(), response);
    }

    @Override
    public void download(String code, HttpServletResponse response) {
        DigitalResource resource = analyzeCode(code);
        objectStorageService.download(resource.getOriginalName(), resource.getFileName(), response);
    }

    @Override
    public byte[] load(String code) {
        DigitalResource resource = analyzeCode(code);
        return objectStorageService.load(resource.getFileName());
    }

    @Override
    public String uploadFile(String type, MultipartFile file) {
        return uploadFile(type, FileResourceUtil.getOriginalName(file), MediaUtil.getBytes(file));
    }

    @Override
    public String uploadFile(String type, String originalName, byte[] data) {
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
        String fileName = objectStorageService.upload(originalName, data);
        return digitalResourceService.createResource(type, originalName, fileName).getCode();
    }

    @Override
    public String uploadImage(String type, MultipartFile file) {
        return uploadImage(type, FileResourceUtil.getOriginalName(file), MediaUtil.getBytes(file));
    }

    @Override
    public String uploadImage(String type, String originalName, byte[] data) {
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
        String fileName = objectStorageService.upload(originalName, data);
        return digitalResourceService.createResource(type, originalName, fileName).getCode();
    }
}
