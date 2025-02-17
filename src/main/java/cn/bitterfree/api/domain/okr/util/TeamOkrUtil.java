package cn.bitterfree.api.domain.okr.util;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import cn.bitterfree.api.domain.center.util.CacheDelayClearUtil;
import cn.bitterfree.api.domain.okr.constants.OkrConstants;
import cn.bitterfree.api.domain.okr.model.entity.TeamOkr;
import cn.bitterfree.api.domain.okr.service.TeamOkrService;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.redis.cache.RedisListCache;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 0:38
 */
@Slf4j
public class TeamOkrUtil {

    private final static RedisCache REDIS_CACHE = SpringUtil.getBean(RedisCache.class);
    private final static RedisListCache REDIS_LIST_CACHE = SpringUtil.getBean(RedisListCache.class);
    private final static TeamOkrService TEAM_OKR_SERVICE = SpringUtil.getBean(TeamOkrService.class);

    public static Long getTeamRootId(Long id) {
        String redisKey = OkrConstants.TEAM_ROOT_MAP + id;
        return REDIS_CACHE.getObject(redisKey, Long.class).orElseGet(() -> {
            TeamOkr rootTeam = TEAM_OKR_SERVICE.findRootTeam(id);
            Long rootTeamId = rootTeam.getId();
            REDIS_CACHE.setObject(redisKey, rootTeamId, OkrConstants.TEAM_ROOT_TTL, OkrConstants.TEAM_ROOT_TTL_UNIT);
            return rootTeam.getId();
        });
    }

    public static List<Long> getChildIds(Long id) {
        String redisKey = OkrConstants.TEAM_CHILD_LIST + id;
        return REDIS_LIST_CACHE.getList(redisKey, Long.class).orElseGet(() -> {
            List<TeamOkr> teamOkrs = TEAM_OKR_SERVICE.selectChildTeams(id);
            List<Long> ids = teamOkrs.stream().parallel().map(TeamOkr::getId).collect(Collectors.toList());
            REDIS_LIST_CACHE.init(redisKey, ids, OkrConstants.TEAM_CHILD_TTL, OkrConstants.TEAM_CHILD_TTL_UNIT);
            return ids;
        });
    }

    public static List<Long> getAllChildIds(Long id) {
        Long rootId = getTeamRootId(id);
        return getChildIds(rootId);
    }

    public static void deleteChildListCache(Long teamId) {
        // 这里应该是获取到老的缓存 ids，不过缺少的那些也没必要删，因为本来就没有缓存
        log.info("清除 {} 所在团队树的缓存", teamId);
        List<String> redisKeys = getAllChildIds(teamId).stream().map(id -> OkrConstants.TEAM_CHILD_LIST + id).toList();
        REDIS_CACHE.deleteObjects(redisKeys);
        // 延时再次删除（延时双删的方式保证双写一致）
        CacheDelayClearUtil.delayClear(redisKeys);
    }

    public static String getTeamName(Long id) {
        String redisKey = OkrConstants.TEAM_ID_NAME_MAP + id;
        return REDIS_CACHE.getObject(redisKey, String.class).orElseGet(() -> {
            String teamName = Db.lambdaQuery(TeamOkr.class).eq(TeamOkr::getId, id).oneOpt().orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS)).getTeamName();
            REDIS_CACHE.setObject(redisKey, teamName, OkrConstants.TEAM_ID_NAME_TTL, OkrConstants.TEAM_ID_NAME_UNIT);
            return teamName;
        });
    }

    public static Long getManagerId(Long teamId) {
        // 由于团队的管理者暂时不可变，所以设置缓存
        String redisKey = OkrConstants.TEAM_ID_MANAGER_MAP + teamId;
        return REDIS_CACHE.getObject(redisKey, Long.class).orElseGet(() -> {
            Long managerId = Db.lambdaQuery(TeamOkr.class).eq(TeamOkr::getId, teamId).oneOpt().orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS)).getManagerId();
            REDIS_CACHE.setObject(redisKey, managerId, OkrConstants.TEAM_ID_MANAGER_TTL, OkrConstants.TEAM_ID_MANAGER_UNIT);
            return managerId;
        });
    }
}
