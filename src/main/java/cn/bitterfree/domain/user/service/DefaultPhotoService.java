package cn.bitterfree.domain.user.service;

import cn.bitterfree.domain.user.model.entity.DefaultPhoto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【default_photo(默认头像表)】的数据库操作Service
* @createDate 2025-01-17 16:33:39
*/
public interface DefaultPhotoService extends IService<DefaultPhoto> {

    List<String> getDefaultPhotoList();

    void remove(String code);

    void add(String code);

}
