package cn.bitterfree.domain.media.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-11-16
 * Time: 13:06
 */
public interface FileMediaService {

    String analyzeCode(String code);
    void preview(String code, HttpServletResponse response);
    byte[] load(String code);

    void remove(String code);
    void remove(List<String> codeList);

    String uploadFile(MultipartFile file);
    String uploadFile(MultipartFile file, Long activeLimit);
    String uploadFile(String originalName, byte[] data);
    String uploadFile(String originalName, byte[] data, Long activeLimit);
    String uploadImage(MultipartFile file);
    String uploadImage(MultipartFile file, Long activeLimit);
    String uploadImage(String originalName, byte[] data);
    String uploadImage(String originalName, byte[] data, Long activeLimit);

}
