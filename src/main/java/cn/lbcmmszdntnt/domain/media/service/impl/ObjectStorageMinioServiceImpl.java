package cn.lbcmmszdntnt.domain.media.service.impl;



import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.media.service.ObjectStorageService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.monio.engine.MinioEngine;
import cn.lbcmmszdntnt.common.util.media.FileResourceUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

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
            minioEngine.remove(fileName);
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage(), GlobalServiceStatusCode.FILE_RESOURCE_REMOVE_FAILED);
        }
    }

}
