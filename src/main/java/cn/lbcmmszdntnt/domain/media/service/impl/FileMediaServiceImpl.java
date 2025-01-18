package cn.lbcmmszdntnt.domain.media.service.impl;

import cn.lbcmmszdntnt.common.enums.FileResourceType;
import cn.lbcmmszdntnt.common.util.media.FileResourceUtil;
import cn.lbcmmszdntnt.common.util.media.ImageUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.config.ResourceCompressionConfig;
import cn.lbcmmszdntnt.domain.media.model.entity.DigitalResource;
import cn.lbcmmszdntnt.domain.media.service.DigitalResourceService;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.media.service.ObjectStorageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static cn.lbcmmszdntnt.domain.media.constants.FileMediaConstants.DEFAULT_ACTIVE_LIMIT;

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
public final class FileMediaServiceImpl implements FileMediaService {

    private final ResourceCompressionConfig resourceCompressionConfig;

    private final DigitalResourceService digitalResourceService;

    private final ObjectStorageService objectStorageService;

    @Override
    public String analyzeCode(String code) {
        return digitalResourceService.getFileName(code);
    }

    @Override
    public void preview(String code, HttpServletResponse response) {
        objectStorageService.preview(analyzeCode(code), response);
    }

    @Override
    public byte[] load(String code) {
        return objectStorageService.load(analyzeCode(code));
    }

    @Override
    public void remove(String code) {
        remove(List.of(code));
    }

    @Override
    public void remove(List<String> codeList) {
        log.info("删除资源 {}", codeList);
        if(CollectionUtils.isEmpty(codeList)) {
            return;
        }
        List<String> fileNameList = digitalResourceService.lambdaQuery()
                .in(DigitalResource::getCode, codeList)
                .list()
                .stream()
                .map(DigitalResource::getFileName)
                .toList();
        objectStorageService.remove(fileNameList);
        digitalResourceService.removeResource(codeList);
    }

    @Override
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, DEFAULT_ACTIVE_LIMIT);
    }

    @Override
    public String uploadFile(MultipartFile file, Long activeLimit) {
        return uploadFile(FileResourceUtil.getOriginalName(file), MediaUtil.getBytes(file), activeLimit);
    }

    @Override
    public String uploadFile(String originalName, byte[] data) {
        return uploadFile(originalName, data, DEFAULT_ACTIVE_LIMIT);
    }

    @Override
    public String uploadFile(String originalName, byte[] data, Long activeLimit) {
        // 判断文件类型
        String contentType = MediaUtil.getContentType(data);
        String suffix = null;
        // 判断是否是图片类型，并判断是否达到压缩阈值（否则压缩适得其反），若是则压缩图片
        if(FileResourceUtil.matchType(contentType, FileResourceType.IMAGE) && resourceCompressionConfig.getThreshold().compareTo(data.length) <= 0) {
            // 压缩图片
            data = ImageUtil.compressImage(data);
            suffix = ImageUtil.COMPRESS_FORMAT_SUFFIX;
            originalName = FileResourceUtil.changeSuffix(originalName, suffix);
        } else {
            // 使用原后缀
            suffix = FileResourceUtil.getSuffix(originalName);
        }
        String fileName = objectStorageService.upload(originalName, data);
        return digitalResourceService.createResource(originalName, fileName, activeLimit).getCode();
    }

    @Override
    public String uploadImage(MultipartFile file) {
        return uploadImage(file, DEFAULT_ACTIVE_LIMIT);
    }

    @Override
    public String uploadImage(MultipartFile file, Long activeLimit) {
        return uploadImage(FileResourceUtil.getOriginalName(file), MediaUtil.getBytes(file), activeLimit);
    }

    @Override
    public String uploadImage(String originalName, byte[] data) {
        return uploadImage(originalName, data, DEFAULT_ACTIVE_LIMIT);
    }

    @Override
    public String uploadImage(String originalName, byte[] data, Long activeLimit) {
        // 判断文件类型
        String contentType = MediaUtil.getContentType(data);
        String suffix = null;
        // 判断是否是图片类型
        FileResourceUtil.checkImage(contentType);
        // 判断是否压缩
        if(resourceCompressionConfig.getThreshold().compareTo(data.length) <= 0) {
            // 压缩图片
            data = ImageUtil.compressImage(data);
            suffix = ImageUtil.COMPRESS_FORMAT_SUFFIX;
            originalName = FileResourceUtil.changeSuffix(originalName, suffix);
        } else {
            // 使用原后缀
            suffix = FileResourceUtil.getSuffix(originalName);
        }
        String fileName = objectStorageService.upload(originalName, data);
        return digitalResourceService.createResource(originalName, fileName, activeLimit).getCode();
    }
}
