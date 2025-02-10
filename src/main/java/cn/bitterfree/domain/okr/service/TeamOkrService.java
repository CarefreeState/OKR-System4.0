package cn.bitterfree.domain.okr.service;

import cn.bitterfree.domain.core.model.vo.OKRCreateVO;
import cn.bitterfree.domain.okr.model.entity.TeamOkr;
import cn.bitterfree.domain.okr.model.vo.TeamOkrStatisticVO;
import cn.bitterfree.domain.okr.model.vo.TeamOkrVO;
import cn.bitterfree.domain.user.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【team_okr(团队 OKR 表)】的数据库操作Service
* @createDate 2024-01-20 02:25:52
*/
public interface TeamOkrService extends IService<TeamOkr> {

    List<TeamOkr> selectChildTeams(Long id);
    TeamOkr findRootTeam(Long id);
    List<TeamOkrVO> getTeamOkrList(User user);

    void checkManager(Long teamId, Long managerId);

    OKRCreateVO grantTeamForMember(Long teamId, Long managerId, Long userId, String teamName);
    List<TeamOkrStatisticVO> countCompletionRate(List<Long> ids);
    void deleteTeamNameCache(Long teamId);

}