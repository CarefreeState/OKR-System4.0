package cn.lbcmmszdntnt.domain.media.service;

import cn.lbcmmszdntnt.domain.media.model.entity.DigitalResource;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 马拉圈
* @description 针对表【digital_resource(资源表)】的数据库操作Service
* @createDate 2024-12-29 23:27:24
*/
public interface DigitalResourceService extends IService<DigitalResource> {

    DigitalResource createResource(String originalName, String fileName, Long activeLimit);
    DigitalResource createResource(String originalName, String fileName);

    void removeResource(String code);

    DigitalResource getResourceByCode(String code);
    String getFileName(String code);
}
