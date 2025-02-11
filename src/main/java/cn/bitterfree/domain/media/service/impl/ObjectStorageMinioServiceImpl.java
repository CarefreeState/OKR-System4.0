package cn.bitterfree.domain.media.service.impl;


import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.common.util.media.FileResourceUtil;
import cn.bitterfree.domain.media.service.ObjectStorageService;
import cn.bitterfree.monio.engine.MinioEngine;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-23
 * Time: 10:32
 */

/**
 * 本服务由 SPI 加载，故必须提供无参构造方法
 */
@Slf4j
public class ObjectStorageMinioServiceImpl implements ObjectStorageService {

    @Resource
    private MinioEngine minioEngine;

    @Override
    public String upload(String originalName, byte[] bytes) {
        try {
            String uniqueName = FileResourceUtil.getUniqueFileName(FileResourceUtil.getSuffix(originalName));
            // 上传资源
            minioEngine.upload(uniqueName, bytes);
            return uniqueName;
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage(), GlobalServiceStatusCode.FILE_RESOURCE_UPLOAD_FAILED);
        }
    }

    @Override
    public byte[] load(String fileName) {
        try {
            return minioEngine.load(fileName);
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage(), GlobalServiceStatusCode.FILE_RESOURCE_LOAD_FAILED);
        }
    }

    @Override
    public void preview(String fileName, HttpServletResponse response) {
        try {
            minioEngine.preview(fileName, response);
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage(), GlobalServiceStatusCode.FILE_RESOURCE_PREVIEW_FAILED);
        }
    }

    @Override
    public void download(String downloadName, String fileName, HttpServletResponse response) {
        try {
            minioEngine.download(downloadName, fileName, response);
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage(), GlobalServiceStatusCode.FILE_RESOURCE_DOWNLOAD_FAILED);
        }
    }

    @Override
    public void remove(String fileName) {
        try {
            log.info("删除资源 {}", fileName);
            minioEngine.remove(fileName);
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage(), GlobalServiceStatusCode.FILE_RESOURCE_REMOVE_FAILED);
        }
    }

    @Override
    public void remove(List<String> fileNameList) {
        fileNameList.forEach(this::remove);
    }

}
