package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.juc.threadpool.IOThreadPool;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.user.constants.UserConstants;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserPhotoService;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.config.RedisLockProperties;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 2:15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserPhotoServiceImpl implements UserPhotoService {

    private final RedisLock redisLock;

    private final UserService userService;

    private final RedisLockProperties redisLockProperties;

    private final FileMediaService fileMediaService;

    private String uploadPhoto(MultipartFile multipartFile, Long userId, String originPhoto) {
        // 删除原头像
        IOThreadPool.submit(() -> {
            fileMediaService.remove(originPhoto);
        });
        // 下载头像到本地
        String code = fileMediaService.uploadImage(multipartFile);
        // 修改数据库
        userService.lambdaUpdate()
                .set(User::getPhoto, code)
                .eq(User::getId, userId)
                .update();
        userService.deleteUserAllCache(userId);
        return code;
    }

    @Override
    public String tryUploadPhoto(MultipartFile multipartFile, Long userId, String originPhoto) {
        String lock = UserConstants.USER_PHOTO_LOCK + userId;
        return redisLock.tryLockGetSomething(lock, 0L, redisLockProperties.getTimeout(), TimeUnit.SECONDS,
                () -> uploadPhoto(multipartFile, userId, originPhoto),
                () -> {
                    throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
                });
    }
}
