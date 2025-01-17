package cn.lbcmmszdntnt.domain.okr.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.okr.constants.OkrConstants;
import cn.lbcmmszdntnt.domain.okr.model.entity.TeamOkr;
import cn.lbcmmszdntnt.domain.okr.service.TeamOkrService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.mq.sender.RabbitMQSender;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import cn.lbcmmszdntnt.redis.cache.RedisListCache;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static cn.lbcmmszdntnt.domain.okr.constants.OkrConstants.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 0:38
 */
@Component
@Slf4j
public class TeamOkrUtil {

    private final static RedisCache REDIS_CACHE = SpringUtil.getBean(RedisCache.class);
    private final static RedisListCache REDIS_LIST_CACHE = SpringUtil.getBean(RedisListCache.class);
    private final static TeamOkrService TEAM_OKR_SERVICE = SpringUtil.getBean(TeamOkrService.class);
    private final static RabbitMQSender RABBIT_MQ_SENDER = SpringUtil.getBean(RabbitMQSender.class);

    public static Long getTeamRootId(Long id) {
        String redisKey = TEAM_ROOT_MAP + id;
        return REDIS_CACHE.getObject(redisKey, Long.class).orElseGet(() -> {
            TeamOkr rootTeam = TEAM_OKR_SERVICE.findRootTeam(id);
            Long rootTeamId = rootTeam.getId();
            REDIS_CACHE.setObject(redisKey, rootTeamId, TEAM_ROOT_TTL, TEAM_ROOT_TTL_UNIT);
            return rootTeam.getId();
        });
    }

    public static List<Long> getChildIds(Long id) {
        String redisKey = TEAM_CHILD_LIST + id;
        return REDIS_LIST_CACHE.getList(redisKey, Long.class).orElseGet(() -> {
            List<TeamOkr> teamOkrs = TEAM_OKR_SERVICE.selectChildTeams(id);
            List<Long> ids = teamOkrs.stream().parallel().map(TeamOkr::getId).collect(Collectors.toList());
            REDIS_LIST_CACHE.init(redisKey, ids, TEAM_CHILD_TTL, TEAM_CHILD_TTL_UNIT);
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
        List<String> redisKeys = getAllChildIds(teamId).stream().map(id -> TEAM_CHILD_LIST + id).toList();
        REDIS_CACHE.deleteObjects(redisKeys);
    }

    public static void sendTeamOkrClearCache(Long teamId) {
        RABBIT_MQ_SENDER.sendDelayMessage(
                OkrConstants.TEAM_OKR_CLEAR_CACHE_DELAY_DIRECT,
                OkrConstants.TEAM_OKR_CLEAR_CACHE,
                teamId,
                OkrConstants.TEAM_OKR_CLEAR_CACHE_DELAY
        );
    }

    public static String getTeamName(Long id) {
        String redisKey = TEAM_ID_NAME_MAP + id;
        return REDIS_CACHE.getObject(redisKey, String.class).orElseGet(() -> {
            String teamName = Db.lambdaQuery(TeamOkr.class).eq(TeamOkr::getId, id).oneOpt().orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS)).getTeamName();
            REDIS_CACHE.setObject(redisKey, teamName, TEAM_ID_NAME_TTL, TEAM_ID_NAME_UNIT);
            return teamName;
        });
    }

    public static Long getManagerId(Long teamId) {
        // 由于团队的管理者暂时不可变，所以设置缓存
        String redisKey = TEAM_ID_MANAGER_MAP + teamId;
        return REDIS_CACHE.getObject(redisKey, Long.class).orElseGet(() -> {
            Long managerId = Db.lambdaQuery(TeamOkr.class).eq(TeamOkr::getId, teamId).oneOpt().orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS)).getManagerId();
            REDIS_CACHE.setObject(redisKey, managerId, TEAM_ID_MANAGER_TTL, TEAM_ID_MANAGER_UNIT);
            return managerId;
        });
    }
}
