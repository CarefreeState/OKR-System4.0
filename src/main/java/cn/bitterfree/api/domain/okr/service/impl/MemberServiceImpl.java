package cn.bitterfree.api.domain.okr.service.impl;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.okr.constants.OkrConstants;
import cn.bitterfree.api.domain.okr.model.entity.TeamOkr;
import cn.bitterfree.api.domain.okr.model.entity.TeamPersonalOkr;
import cn.bitterfree.api.domain.okr.model.mapper.TeamPersonalOkrMapper;
import cn.bitterfree.api.domain.okr.model.vo.TeamPersonalOkrVO;
import cn.bitterfree.api.domain.okr.service.MemberService;
import cn.bitterfree.api.domain.okr.util.TeamOkrUtil;
import cn.bitterfree.api.redis.cache.RedisMapCache;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 21:45
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final TeamPersonalOkrMapper teamPersonalOkrMapper;

    private final RedisMapCache redisMapCache;

    @Override
    public Boolean findExistsInTeam(List<Long> ids, Long userId) {
        return teamPersonalOkrMapper.getTeamPersonalOkrList(userId).stream()
                .parallel()
                .map(TeamPersonalOkrVO::getTeamId)
                .anyMatch(ids::contains);
    }

    @Override
    public void checkExistsInTeam(Long teamId, Long userId) {
        Boolean isExists = isExistsInTeam(teamId, userId);
        if(Boolean.FALSE.equals(isExists)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.NON_TEAM_MEMBER);
        }
    }

    @Override
    public Boolean isExistsInTeam(Long teamId, Long userId) {
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 查看是否有缓存
        String redisKey = OkrConstants.USER_TEAM_MEMBER + rootId;
        return redisMapCache.get(redisKey, userId, Boolean.class).orElseGet(() -> {
            List<Long> ids = TeamOkrUtil.getChildIds(rootId);
            Boolean isExists = findExistsInTeam(ids, userId);
            redisMapCache.getMap(redisKey, Long.class, Boolean.class).orElseGet(() -> {
                Map<Long, Boolean> data = new HashMap<>();
                redisMapCache.init(redisKey, data, OkrConstants.USER_TEAM_MEMBER_TTL, OkrConstants.USER_TEAM_MEMBER_TTL_UNIT);
                return null;
            });
            redisMapCache.put(redisKey, userId, isExists);
            return isExists;
        });
    }

    @Override
    public Boolean haveExtendTeam(Long teamId, Long userId) {
        TeamOkr teamOkr = Db.lambdaQuery(TeamOkr.class)
                .eq(TeamOkr::getParentTeamId, teamId)
                .eq(TeamOkr::getManagerId, userId)
                .one();
        return Objects.nonNull(teamOkr);
    }

    @Override
    public void setExistsInTeam(Long teamId, Long userId) {
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 查看是否有缓存
        String redisKey = OkrConstants.USER_TEAM_MEMBER + rootId;
        redisMapCache.put(redisKey, userId, Boolean.TRUE);
    }

    @Override
    public void setNotExistsInTeam(Long teamId, Long userId) {
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 查看是否有缓存
        String redisKey = OkrConstants.USER_TEAM_MEMBER + rootId;
        redisMapCache.put(redisKey, userId, Boolean.FALSE);
    }

    @Override
    public void removeMember(Long teamId, Long memberOkrId, Long userId) {
        // 判断是否扩展了
        Boolean isExtend = haveExtendTeam(teamId, userId);
        if(Boolean.TRUE.equals(isExtend)) {
            // 无法删除
            throw new GlobalServiceException(GlobalServiceStatusCode.MEMBER_CANNOT_REMOVE);
        }
        // 删除
        Db.lambdaUpdate(TeamPersonalOkr.class)
                .eq(TeamPersonalOkr::getId, memberOkrId)
                .remove();
        // 设置为不存在
        setNotExistsInTeam(teamId, userId);
    }

}
