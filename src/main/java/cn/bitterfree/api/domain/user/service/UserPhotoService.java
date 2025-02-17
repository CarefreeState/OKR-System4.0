package cn.bitterfree.api.domain.user.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.function.Supplier;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 2:15
 */
public interface UserPhotoService {

    String getAnyOnePhoto();

    String tryUploadPhoto(MultipartFile multipartFile, Long userId, String originPhoto);
    String updateUserPhoto(Supplier<String> getCode, Long userId, String originPhoto);

}
