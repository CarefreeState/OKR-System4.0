package cn.lbcmmszdntnt.domain.media.service;

import cn.lbcmmszdntnt.domain.media.model.entity.DigitalResource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-16
 * Time: 13:06
 */
public interface FileMediaService {

    DigitalResource analyzeCode(String code);
    void preview(String code, HttpServletResponse response);
    void download(String code, HttpServletResponse response);
    byte[] load(String code);

    String uploadFile(String type, MultipartFile file);
    String uploadFile(String type, String originalName, byte[] data);
    String uploadImage(String type, MultipartFile file);
    String uploadImage(String type, String originalName, byte[] data);

}
