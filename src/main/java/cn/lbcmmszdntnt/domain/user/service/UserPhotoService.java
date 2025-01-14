package cn.lbcmmszdntnt.domain.user.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 2:15
 */
public interface UserPhotoService {

    String tryUploadPhoto(MultipartFile multipartFile, Long userId, String originPhoto);

}
