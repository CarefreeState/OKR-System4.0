package cn.bitterfree.domain.user.service.impl;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.domain.media.service.FileMediaService;
import cn.bitterfree.domain.user.constants.UserConstants;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.domain.user.service.DefaultPhotoService;
import cn.bitterfree.domain.user.service.UserPhotoService;
import cn.bitterfree.domain.user.service.UserService;
import cn.bitterfree.redis.config.RedisLockProperties;
import cn.bitterfree.redis.lock.RedisLock;
import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

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

    private final DefaultPhotoService defaultPhotoService;

    @Override
    public String getAnyOnePhoto() {
        List<String> defaultPhotoList = defaultPhotoService.getDefaultPhotoList();
        return !CollectionUtils.isEmpty(defaultPhotoList) ?
                defaultPhotoList.get(RandomUtil.randomInt(defaultPhotoList.size())) :
                UserConstants.DEFAULT_PHOTO;
    }

    @Override
    public String tryUploadPhoto(MultipartFile multipartFile, Long userId, String originPhoto) {
        return updateUserPhoto(() -> fileMediaService.uploadImage(multipartFile), userId, originPhoto);
    }

    @Override
    public String updateUserPhoto(Supplier<String> getCode, Long userId, String originPhoto) {
        String lock = UserConstants.USER_PHOTO_LOCK + userId; // 避免因为上传过慢，上传了多次重复的
        return redisLock.tryLockGetSomething(lock, 0L, redisLockProperties.getTimeout(), TimeUnit.SECONDS, () -> {
            // 获取头像资源码
            String code = getCode.get();
            // 删除原头像
            IOThreadPool.submit(() -> {
                if(!code.equals(originPhoto) && !defaultPhotoService.getDefaultPhotoList().contains(originPhoto)) {
                    fileMediaService.remove(originPhoto);
                }
            });
            // 修改数据库
            userService.lambdaUpdate()
                    .set(User::getPhoto, code)
                    .eq(User::getId, userId)
                    .update();
            userService.clearUserAllCache(userId);
            return code;
        }, () -> {
            throw new GlobalServiceException(GlobalServiceStatusCode.REDIS_LOCK_FAIL);
        });
    }
}
