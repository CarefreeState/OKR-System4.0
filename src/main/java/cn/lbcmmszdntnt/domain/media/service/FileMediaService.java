package cn.lbcmmszdntnt.domain.media.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-16
 * Time: 13:06
 */
public interface FileMediaService {

    String uploadFile(MultipartFile file);

    String uploadFile(String originalName, byte[] data);

    String uploadImage(MultipartFile file);

    String uploadImage(String originalName, byte[] data);

}
