package cn.lbcmmszdntnt.domain.user.service.impl;

import cn.lbcmmszdntnt.common.util.juc.threadpool.IOThreadPool;
import cn.lbcmmszdntnt.domain.media.service.FileMediaService;
import cn.lbcmmszdntnt.domain.user.model.entity.DefaultPhoto;
import cn.lbcmmszdntnt.domain.user.model.mapper.DefaultPhotoMapper;
import cn.lbcmmszdntnt.domain.user.service.DefaultPhotoService;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.redis.cache.RedisListCache;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import cn.lbcmmszdntnt.redis.lock.strategy.ReadLockStrategy;
import cn.lbcmmszdntnt.redis.lock.strategy.WriteLockStrategy;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static cn.lbcmmszdntnt.domain.user.constants.UserConstants.*;

/**
* @author 马拉圈
* @description 针对表【default_photo(默认头像表)】的数据库操作Service实现
* @createDate 2025-01-17 16:33:39
*/
@Service
@RequiredArgsConstructor
public class DefaultPhotoServiceImpl extends ServiceImpl<DefaultPhotoMapper, DefaultPhoto>
    implements DefaultPhotoService{

    private final RedisCache redisCache;
    private final RedisListCache redisListCache;
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
            fileMediaService.remove(code);
        }, () -> {}, writeLockStrategy);
    }

    @Override
    public void add(String code) {
        redisLock.tryLockDoSomething(DEFAULT_PHOTO_LIST_LOCK, () -> {
            DefaultPhoto defaultPhoto = new DefaultPhoto();
            defaultPhoto.setCode(code);
            this.save(defaultPhoto);
            redisCache.deleteObject(DEFAULT_PHOTO_LIST_CACHE);
            IOThreadPool.submit(() -> {
                // 删除 code 但不希望影响速度与结果
                fileMediaService.remove(code);
            });
        }, () -> {}, writeLockStrategy);
    }

}




