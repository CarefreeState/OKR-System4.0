package cn.lbcmmszdntnt.domain.okr.model.mapper;

import cn.lbcmmszdntnt.domain.core.model.vo.inner.UserStatusFlagsVO;
import cn.lbcmmszdntnt.domain.okr.model.entity.TeamOkr;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamOkrStatisticVO;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamOkrVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【team_okr(团队 OKR 表)】的数据库操作Mapper
* @createDate 2024-01-20 02:25:52
*/
public interface TeamOkrMapper extends BaseMapper<TeamOkr> {

    List<TeamOkr> queryTeamTree(@Param("id") Long id);
    TeamOkr findTeamRoot(@Param("id") Long id);
    List<TeamOkrVO> getTeamOkrList(@Param("id") Long id);
    List<TeamOkrStatisticVO> selectKeyResultsByTeamId(@Param("ids") List<Long> ids);
    List<UserStatusFlagsVO> getStatusFlagsByUserId(@Param("ids") List<Long> ids);

    void createFindTeamRootProcedure();
    void creatQueryTeamTreeProcedure();
    void createPrepareTeamTreeProcedure();

}




