package cn.bitterfree.api.domain.media.service.impl;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.common.util.convert.ObjectUtil;
import cn.bitterfree.api.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.api.domain.center.util.CacheDelayClearUtil;
import cn.bitterfree.api.domain.media.constants.FileMediaConstants;
import cn.bitterfree.api.domain.media.generator.DigitalResourceCodeBloomFilter;
import cn.bitterfree.api.domain.media.generator.DigitalResourceCodeGenerator;
import cn.bitterfree.api.domain.media.model.entity.DigitalResource;
import cn.bitterfree.api.domain.media.model.mapper.DigitalResourceMapper;
import cn.bitterfree.api.domain.media.service.DigitalResourceService;
import cn.bitterfree.api.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author 马拉圈
* @description 针对表【digital_resource(资源表)】的数据库操作Service实现
* @createDate 2024-12-29 23:27:24
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class DigitalResourceServiceImpl extends ServiceImpl<DigitalResourceMapper, DigitalResource>
    implements DigitalResourceService {

    private final RedisCache redisCache;

    private final DigitalResourceCodeGenerator digitalResourceCodeGenerator;

    private final DigitalResourceCodeBloomFilter digitalResourceCodeBloomFilter;

    @Override
    public DigitalResource createResource(String originalName, String fileName, Long activeLimit) {
        DigitalResource digitalResource = new DigitalResource();
        String code = digitalResourceCodeGenerator.generate();
        digitalResource.setCode(code);
        digitalResource.setOriginalName(originalName);
        digitalResource.setFileName(fileName);
        digitalResource.setActiveLimit(activeLimit);
        this.save(digitalResource);
        // 删除 code 的缓存
        clearCache(List.of(code));
        return digitalResource;
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
        // 布隆过滤器 + 缓存 null 的方式预防缓存穿透
        String fileName = "";
        if (digitalResourceCodeBloomFilter.contains(code)) {
            if (redisCache.isExists(redisKey)) {
                fileName = redisCache.getObject(redisKey, String.class).orElse(null);
            } else {
                fileName = getResourceByCode(code).getFileName();
                redisCache.setObject(redisKey, fileName, FileMediaConstants.CODE_RESOURCE_MAP_TIMEOUT, FileMediaConstants.CODE_RESOURCE_MAP_TIMEUNIT);
            }
        }
        if (StringUtils.hasText(fileName)) {
            return fileName;
        } else {
            throw new GlobalServiceException(GlobalServiceStatusCode.RESOURCE_NOT_EXISTS);
        }
    }

    @Override
    public void removeResource(List<String> codeList) {
        log.info("删除资源 {}", codeList);
        if(CollectionUtils.isEmpty(codeList)) {
            return;
        }
        this.lambdaUpdate()
                .in(DigitalResource::getCode, codeList)
                .remove();
        clearCache(codeList);
    }

    @Override
    public void clearCache(List<String> codeList) {
        List<String> redisKeys = ObjectUtil.distinctNonNullStream(codeList)
                .filter(StringUtils::hasText)
                .map(code -> FileMediaConstants.CODE_RESOURCE_MAP + code)
                .toList();
        redisCache.deleteObjects(redisKeys);
        CacheDelayClearUtil.delayClear(redisKeys); // 延时双删
    }

}




