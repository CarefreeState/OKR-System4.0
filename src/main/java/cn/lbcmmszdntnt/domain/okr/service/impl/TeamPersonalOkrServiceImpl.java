package cn.lbcmmszdntnt.domain.okr.service.impl;


import cn.lbcmmszdntnt.common.constants.SuppressWarningsValue;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.core.model.dto.OkrOperateDTO;
import cn.lbcmmszdntnt.domain.core.model.vo.OkrCoreVO;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.okr.config.CoreUserMapConfig;
import cn.lbcmmszdntnt.domain.okr.model.mapper.TeamPersonalOkrMapper;
import cn.lbcmmszdntnt.domain.okr.model.po.TeamPersonalOkr;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamMemberVO;
import cn.lbcmmszdntnt.domain.okr.model.vo.TeamPersonalOkrVO;
import cn.lbcmmszdntnt.domain.okr.service.MemberService;
import cn.lbcmmszdntnt.domain.okr.service.OkrOperateService;
import cn.lbcmmszdntnt.domain.okr.service.TeamPersonalOkrService;
import cn.lbcmmszdntnt.domain.qrcode.factory.InviteQRCodeServiceFactory;
import cn.lbcmmszdntnt.domain.qrcode.service.InviteQRCodeService;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 马拉圈
* @description 针对表【team_personal_okr(创建团队个人 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings(value = SuppressWarningsValue.SPRING_JAVA_INJECTION_POINT_AUTOWIRING_INSPECTION)
public class TeamPersonalOkrServiceImpl extends ServiceImpl<TeamPersonalOkrMapper, TeamPersonalOkr>
    implements TeamPersonalOkrService, OkrOperateService {

    private final TeamPersonalOkrMapper teamPersonalOkrMapper;

    private final OkrCoreService okrCoreService;

    private final MemberService memberService;

    private final RedisCache redisCache;

    private final InviteQRCodeServiceFactory inviteQRCodeServiceFactory;

    @Override
    public Map<String, Object> createOkrCore(User user, OkrOperateDTO okrOperateDTO) {
        // 检测密钥
        Long teamId = okrOperateDTO.getTeamOkrId();
        String secret = okrOperateDTO.getSecret();
        String type = okrOperateDTO.getType();
        type = StringUtils.hasText(type) ? type : InviteQRCodeServiceFactory.WX_TYPE;
        InviteQRCodeService inviteQRCodeService = inviteQRCodeServiceFactory.getService(type);
        inviteQRCodeService.checkParams(teamId, secret);
        // 获取用户 ID（受邀者）
        Long userId = user.getId();
        // 判断是否可以加入团队
        if(Boolean.TRUE.equals(memberService.isExistsInTeam(teamId, userId))) {
            String message = String.format("用户 %d 无法再次加入团队 %d", userId, teamId);
            throw new GlobalServiceException(message, GlobalServiceStatusCode.REPEATED_JOIN_TEAM);
        }
        // 可以加入团队了
        // 创建一个 OKR 内核
        Long coreId = okrCoreService.createOkrCore();
        // 创建一个团队个人 OKR
        TeamPersonalOkr teamPersonalOkr = new TeamPersonalOkr();
        teamPersonalOkr.setCoreId(coreId);
        teamPersonalOkr.setTeamId(teamId);
        teamPersonalOkr.setUserId(userId);
        teamPersonalOkrMapper.insert(teamPersonalOkr);
        Long id = teamPersonalOkr.getId();
        log.info("用户 {} 新建团队 {} 的 团队个人 OKR {} 内核 {}", userId, teamId, id, coreId);
        // 更新一下缓存
        memberService.setExistsInTeam(teamId, userId);
        return new HashMap<String, Object>() {{
            this.put("id", id);
            this.put("coreId", coreId);
        }};
    }

    @Override
    public OkrCoreVO selectAllOfCore(User user, Long coreId) {
        if(Boolean.TRUE.equals(canVisit(user, coreId))) {
            // 调用服务查询详细信息
            return okrCoreService.searchOkrCore(coreId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
    }

    @Override
    public Boolean canVisit(User user, Long coreId) {
        // 根据 coreId 获取 coreId 使用者（团队个人 OKR 只能由使用者观看）
        return user.getId().equals(getCoreUser(coreId));
    }

    @Override
    public Long getCoreUser(Long coreId) {
        String redisKey = CoreUserMapConfig.USER_CORE_MAP + coreId;
        return redisCache.getObject(redisKey, Long.class).orElseGet(() -> {
                Long userId = Db.lambdaQuery(TeamPersonalOkr.class)
                    .eq(TeamPersonalOkr::getCoreId, coreId)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS)
                    ).getUserId();
                redisCache.setObject(redisKey, userId, CoreUserMapConfig.USER_CORE_MAP_TTL, CoreUserMapConfig.USER_CORE_MAP_TTL_UNIT);
                return userId;
        });
    }

    @Override
    public List<TeamPersonalOkrVO> getTeamPersonalOkrList(User user) {
        // 获取当前用户 id
        Long id = user.getId();
        // 获取团队 OKR 列表
        List<TeamPersonalOkrVO> teamPersonalOkrVOList = teamPersonalOkrMapper.getTeamPersonalOkrList(id);
        log.info("查询用户 {} 的团队个人 OKR 列表 : {} 行", id, teamPersonalOkrVOList.size());
        return teamPersonalOkrVOList;
    }

    @Override
    public List<TeamMemberVO> getTeamMembers(Long id) {
        // 查询团队成员列表
        List<TeamMemberVO> teamMembers = teamPersonalOkrMapper.getTeamMembers(id);
        teamMembers.stream().parallel().forEach(teamMemberVO -> {
            Long userId = teamMemberVO.getUserId();
            teamMemberVO.setIsExtend(memberService.haveExtendTeam(id, userId));
        });
        log.info("查询团队 {} 的成员列表 : {} 行", id, teamMembers.size());
        return teamMembers;
    }


}




