package cn.bitterfree.domain.okr.model.mapper;


import cn.bitterfree.domain.core.model.vo.inner.UserStatusFlagsVO;
import cn.bitterfree.domain.okr.model.entity.PersonalOkr;
import cn.bitterfree.domain.okr.model.vo.PersonalOkrVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【personal_okr(个人 OKR 表)】的数据库操作Mapper
* @createDate 2024-01-20 02:25:52
*/
public interface PersonalOkrMapper extends BaseMapper<PersonalOkr> {

    Long getNotCompletedCount(@Param("id") Long id);
    List<PersonalOkrVO> getPersonalOkrList(@Param("id") Long id);
    List<UserStatusFlagsVO> getStatusFlagsByUserId(@Param("ids") List<Long> ids);

}




