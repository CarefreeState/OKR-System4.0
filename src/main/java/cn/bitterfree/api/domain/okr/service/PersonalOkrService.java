package cn.bitterfree.api.domain.okr.service;


import cn.bitterfree.api.domain.okr.model.entity.PersonalOkr;
import cn.bitterfree.api.domain.okr.model.vo.PersonalOkrVO;
import cn.bitterfree.api.domain.user.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【personal_okr(个人 OKR 表)】的数据库操作Service
* @createDate 2024-01-20 02:25:52
*/
public interface PersonalOkrService extends IService<PersonalOkr> {

    List<PersonalOkrVO> getPersonalOkrList(User user);
}
