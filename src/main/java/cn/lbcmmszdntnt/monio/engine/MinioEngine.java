package cn.lbcmmszdntnt.monio.engine;


import cn.lbcmmszdntnt.common.util.convert.ObjectUtil;
import cn.lbcmmszdntnt.common.util.media.MediaUtil;
import cn.lbcmmszdntnt.common.util.web.HttpRequestUtil;
import cn.lbcmmszdntnt.monio.config.MinioConfig;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MinioEngine {

    private final MinioConfig minioConfig;

    private final MinioClient minioClient;

    /**
     * 文件上传
     */
    public void upload(String fileName, byte[] bytes) throws Exception {
        try(InputStream inputStream = MediaUtil.getInputStream(bytes)) {
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(fileName)
                    .stream(inputStream, bytes.length, -1) // 不分块
                    .contentType(MediaUtil.getContentType(bytes))
                    .build();
            //文件名称相同会覆盖
            minioClient.putObject(objectArgs);
        }
    }

    /**
     * 获取 url
     */
    public String getObjectUrl(String fileName) throws Exception {
        // 查看文件地址
        GetPresignedObjectUrlArgs objectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName)
                .method(Method.GET) //这里必须显式声明请求方法
                .build();
        return minioClient.getPresignedObjectUrl(objectUrlArgs);
    }

    /**
     * 如果是基本的 url，不一定能够公网访问，需要设置 bucket 的权限
     * 如果不隐藏，那就是携带 queryString 的公网链接，是一定能够访问的
     * 隐藏链接 queryString 的访问签名（如果是 true，返回的链接可能会因为对象存储服务器的权限没打开而不多能访问）
     */
    public String getObjectUrl(String fileName, boolean hidden) throws Exception {
        // 查看文件地址
        String objectUrl = getObjectUrl(fileName);
        // 判断是否隐藏
        return Boolean.TRUE.equals(hidden) ? HttpRequestUtil.hiddenQueryString(objectUrl) : objectUrl;
    }

    /**
     * 文件加载
     */
    public byte[] load(String fileName) throws Exception {
        GetObjectArgs objectArgs = GetObjectArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName)
                .build();
        try(GetObjectResponse objectResponse = minioClient.getObject(objectArgs)) {
            return MediaUtil.getBytes(objectResponse);
        }
    }

    /**
     * 文件预览
     */
    public void preview(String fileName, HttpServletResponse response) throws Exception {
        byte[] bytes = load(fileName);
        HttpRequestUtil.returnBytes(bytes, response);
    }

    /**
     * 文件下载
     */
    public void download(String downloadName, String fileName, HttpServletResponse response) throws Exception {
        byte[] bytes = load(fileName);
        HttpRequestUtil.returnBytes(downloadName, bytes, response);
    }

    /**
     * 查看文件对象
     *
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects() throws Exception {
        ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder().bucket(minioConfig.getBucketName()).build();
        List<Item> items = new ArrayList<>();
        for (Result<Item> result : minioClient.listObjects(listObjectsArgs)) {
            items.add(result.get());
        }
        return items;
    }

    /**
     * 删除
     */
    public void remove(String fileName) throws Exception {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName)
                .build();
        minioClient.removeObject(removeObjectArgs);
    }

    /**
     * 删除
     */
    public void remove(List<String> fileNameList) throws Exception {
        RemoveObjectsArgs removeObjectArgs = RemoveObjectsArgs.builder()
                .bucket(minioConfig.getBucketName())
                .objects(ObjectUtil.distinctNonNullStream(fileNameList).filter(StringUtils::hasText).map(DeleteObject::new).toList())
                .build();
        minioClient.removeObjects(removeObjectArgs);
    }

}
