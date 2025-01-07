package cn.lbcmmszdntnt.domain.media.service.impl;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.media.model.entity.DigitalResource;
import cn.lbcmmszdntnt.domain.media.model.mapper.DigitalResourceMapper;
import cn.lbcmmszdntnt.domain.media.service.DigitalResourceService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
* @author 马拉圈
* @description 针对表【digital_resource(资源表)】的数据库操作Service实现
* @createDate 2024-12-29 23:27:24
*/
@Service
@RequiredArgsConstructor
public class DigitalResourceServiceImpl extends ServiceImpl<DigitalResourceMapper, DigitalResource>
    implements DigitalResourceService{

    @Override
    public DigitalResource createResource(String type, String originalName, String fileName) {
        DigitalResource digitalResource = new DigitalResource();
        digitalResource.setCode(UUID.randomUUID().toString().replace("-", ""));
        digitalResource.setOriginalName(originalName);
        digitalResource.setFileName(fileName);
        digitalResource.setType(type);
        this.save(digitalResource);
        return digitalResource;
    }

    @Override
    public DigitalResource getResourceByCode(String code) {
        return this.lambdaQuery()
                .eq(DigitalResource::getCode, code)
                .oneOpt()
                .map(resource -> {
                    // 以 updateTime 字段作为最近访问时间的标识
                    this.lambdaUpdate()
                            .eq(DigitalResource::getId, resource.getId())
                            .set(DigitalResource::getUpdateTime, LocalDateTime.now())
                            .update();
                    return resource;
                })
                .orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.RESOURCE_NOT_EXISTS));
    }

}




