package cn.bitterfree.api.domain.okr.service;


import cn.bitterfree.api.domain.okr.model.entity.TeamPersonalOkr;
import cn.bitterfree.api.domain.okr.model.vo.TeamMemberVO;
import cn.bitterfree.api.domain.okr.model.vo.TeamPersonalOkrVO;
import cn.bitterfree.api.domain.user.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【team_personal_okr(创建团队个人 OKR 表)】的数据库操作Service
* @createDate 2024-01-20 02:25:52
*/
public interface TeamPersonalOkrService extends IService<TeamPersonalOkr> {

    List<TeamPersonalOkrVO> getTeamPersonalOkrList(User user);

    List<TeamMemberVO> getTeamMembers(Long id);

}
