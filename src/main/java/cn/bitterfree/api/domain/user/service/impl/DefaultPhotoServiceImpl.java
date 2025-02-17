package cn.bitterfree.api.domain.user.service.impl;

import cn.bitterfree.api.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.api.domain.media.service.FileMediaService;
import cn.bitterfree.api.domain.user.model.entity.DefaultPhoto;
import cn.bitterfree.api.domain.user.model.mapper.DefaultPhotoMapper;
import cn.bitterfree.api.domain.user.service.DefaultPhotoService;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.redis.cache.RedisListCache;
import cn.bitterfree.api.redis.lock.RedisLock;
import cn.bitterfree.api.redis.lock.strategy.ReadLockStrategy;
import cn.bitterfree.api.redis.lock.strategy.WriteLockStrategy;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static cn.bitterfree.api.domain.user.constants.UserConstants.*;

/**
* @author 马拉圈
* @description 针对表【default_photo(默认头像表)】的数据库操作Service实现
* @createDate 2025-01-17 16:33:39
*/
@Service
@RequiredArgsConstructor
public class DefaultPhotoServiceImpl extends ServiceImpl<DefaultPhotoMapper, DefaultPhoto>
    implements DefaultPhotoService {

    private final RedisCache redisCache;
    private final RedisListCache redisListCache;

    // 读写锁保证双写一致
    private final RedisLock redisLock;
    private final WriteLockStrategy writeLockStrategy;
    private final ReadLockStrategy readLockStrategy;

    private final FileMediaService fileMediaService;

    @Override
    public List<String> getDefaultPhotoList() {
        return redisLock.tryLockGetSomething(DEFAULT_PHOTO_LIST_LOCK, () -> {
            return redisListCache.getList(DEFAULT_PHOTO_LIST_CACHE, String.class).orElseGet(() -> {
                List<String> photoList = list().stream().map(DefaultPhoto::getCode).toList();
                if(!CollectionUtils.isEmpty(photoList)) {
                    redisListCache.init(DEFAULT_PHOTO_LIST_CACHE, photoList, DEFAULT_PHOTO_LIST_TTL, DEFAULT_PHOTO_LIST_UNIT);
                }
                return photoList;
            });
        }, () -> null, readLockStrategy);
    }

    @Override
    public void remove(String code) {
        redisLock.tryLockDoSomething(DEFAULT_PHOTO_LIST_LOCK, () -> {
            this.lambdaUpdate().eq(DefaultPhoto::getCode, code).remove();
            redisCache.deleteObject(DEFAULT_PHOTO_LIST_CACHE);
            IOThreadPool.submit(() -> {
                fileMediaService.remove(code);
            });
        }, () -> {}, writeLockStrategy);
    }

    @Override
    public void add(String code) {
        redisLock.tryLockDoSomething(DEFAULT_PHOTO_LIST_LOCK, () -> {
            DefaultPhoto defaultPhoto = new DefaultPhoto();
            defaultPhoto.setCode(code);
            this.save(defaultPhoto);
            redisCache.deleteObject(DEFAULT_PHOTO_LIST_CACHE);
        }, () -> {}, writeLockStrategy);
    }

}




