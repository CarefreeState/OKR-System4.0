package cn.bitterfree.api.domain.okr.service.impl;


import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.core.model.dto.OkrOperateDTO;
import cn.bitterfree.api.domain.core.model.vo.OKRCreateVO;
import cn.bitterfree.api.domain.core.model.vo.OkrCoreVO;
import cn.bitterfree.api.domain.core.model.vo.inner.UserStatusFlagsVO;
import cn.bitterfree.api.domain.core.service.OkrCoreService;
import cn.bitterfree.api.domain.core.service.OkrOperateService;
import cn.bitterfree.api.domain.okr.constants.OkrConstants;
import cn.bitterfree.api.domain.okr.model.entity.TeamPersonalOkr;
import cn.bitterfree.api.domain.okr.model.mapper.TeamPersonalOkrMapper;
import cn.bitterfree.api.domain.okr.model.vo.TeamMemberVO;
import cn.bitterfree.api.domain.okr.model.vo.TeamPersonalOkrVO;
import cn.bitterfree.api.domain.okr.service.MemberService;
import cn.bitterfree.api.domain.okr.service.TeamPersonalOkrService;
import cn.bitterfree.api.domain.teaminvite.service.TeamInviteIdentifyService;
import cn.bitterfree.api.domain.user.model.entity.User;
import cn.bitterfree.api.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
* @author 马拉圈
* @description 针对表【team_personal_okr(创建团队个人 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamPersonalOkrServiceImpl extends ServiceImpl<TeamPersonalOkrMapper, TeamPersonalOkr>
    implements TeamPersonalOkrService, OkrOperateService {

    private final TeamPersonalOkrMapper teamPersonalOkrMapper;

    private final OkrCoreService okrCoreService;

    private final MemberService memberService;

    private final TeamInviteIdentifyService teamInviteIdentifyService;

    private final RedisCache redisCache;

    @Override
    @Transactional
    public OKRCreateVO createOkrCore(User user, OkrOperateDTO okrOperateDTO) {
        // 检测密钥
        Long teamId = okrOperateDTO.getTeamOkrId();
        String secret = okrOperateDTO.getSecret();
        // 获取用户 ID（受邀者）
        Long userId = user.getId();
        teamInviteIdentifyService.validateSecret(userId, teamId, secret);
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
        return OKRCreateVO.builder().id(id).coreId(coreId).build();
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
        String redisKey = OkrConstants.USER_CORE_MAP + coreId;
        return redisCache.getObject(redisKey, Long.class).orElseGet(() -> {
                Long userId = Db.lambdaQuery(TeamPersonalOkr.class)
                    .eq(TeamPersonalOkr::getCoreId, coreId)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS)
                    ).getUserId();
                redisCache.setObject(redisKey, userId, OkrConstants.USER_CORE_MAP_TTL, OkrConstants.USER_CORE_MAP_TTL_UNIT);
                return userId;
        });
    }

    @Override
    public List<UserStatusFlagsVO> getStatusFlagsByUserId(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return teamPersonalOkrMapper.getStatusFlagsByUserId(ids);
    }

    @Override
    public List<String> mergeUserOkr(Long mainUserId, Long userId) {
        List<String> redisKeys = this.lambdaQuery()
                .eq(TeamPersonalOkr::getUserId, userId)
                .list()
                .stream()
                .map(TeamPersonalOkr::getCoreId)
                .map(uid -> OkrConstants.USER_CORE_MAP + uid)
                .toList();
        // 更新
        this.lambdaUpdate()
                .eq(TeamPersonalOkr::getUserId, userId)
                .set(TeamPersonalOkr::getUserId, mainUserId)
                .update();
        return redisKeys;
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




