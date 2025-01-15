package cn.lbcmmszdntnt.domain.media.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.util.convert.UUIDUtil;
import cn.lbcmmszdntnt.common.util.juc.threadpool.IOThreadPool;
import cn.lbcmmszdntnt.domain.media.constants.FileMediaConstants;
import cn.lbcmmszdntnt.domain.media.model.entity.DigitalResource;
import cn.lbcmmszdntnt.domain.media.model.mapper.DigitalResourceMapper;
import cn.lbcmmszdntnt.domain.media.service.DigitalResourceService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author 马拉圈
* @description 针对表【digital_resource(资源表)】的数据库操作Service实现
* @createDate 2024-12-29 23:27:24
*/
@Service
@RequiredArgsConstructor
public class DigitalResourceServiceImpl extends ServiceImpl<DigitalResourceMapper, DigitalResource>
    implements DigitalResourceService{

    private final RedisCache redisCache;

    @Override
    public DigitalResource createResource(String originalName, String fileName, Long activeLimit) {
        DigitalResource digitalResource = new DigitalResource();
        digitalResource.setCode(UUIDUtil.uuid32());
        digitalResource.setOriginalName(originalName);
        digitalResource.setFileName(fileName);
        digitalResource.setActiveLimit(activeLimit);
        this.save(digitalResource);
        return digitalResource;
    }

    @Override
    public DigitalResource createResource(String originalName, String fileName) {
        return createResource(originalName, fileName, -1L);
    }

    @Override
    public void removeResource(String code) {
        this.lambdaUpdate()
                .eq(DigitalResource::getCode, code)
                .remove();
        redisCache.deleteObject(FileMediaConstants.CODE_RESOURCE_MAP + code);
    }

    @Override
    public DigitalResource getResourceByCode(String code) {
        return this.lambdaQuery()
                .eq(DigitalResource::getCode, code)
                .oneOpt()
                .orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.RESOURCE_NOT_EXISTS));
    }

    @Override
    public String getFileName(String code) {
        IOThreadPool.submit(() -> {
            this.lambdaUpdate()
                    .eq(DigitalResource::getCode, code)
                    .set(DigitalResource::getUpdateTime, new Date())
                    .update();
        });
        String redisKey = FileMediaConstants.CODE_RESOURCE_MAP + code;
        return redisCache.getObject(redisKey, String.class).orElseGet(() -> {
            String fileName = getFileName(code);
            redisCache.setObject(redisKey, fileName, FileMediaConstants.CODE_RESOURCE_MAP_TIMEOUT, FileMediaConstants.CODE_RESOURCE_MAP_TIMEUNIT);
            return fileName;
        });
    }

}




