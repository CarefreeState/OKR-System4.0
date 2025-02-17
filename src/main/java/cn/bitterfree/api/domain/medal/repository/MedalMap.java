package cn.bitterfree.api.domain.medal.repository;


import cn.bitterfree.api.domain.medal.constants.MedalConstants;
import cn.bitterfree.api.domain.medal.model.converter.MedalConverter;
import cn.bitterfree.api.domain.medal.model.entity.Medal;
import cn.bitterfree.api.domain.medal.model.vo.UserMedalVO;
import cn.bitterfree.api.domain.medal.service.MedalService;
import cn.bitterfree.api.redis.cache.RedisMapCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-08
 * Time: 0:22
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class MedalMap {

    private final RedisMapCache redisMapCache;

    private final MedalService medalService;

    public Map<Long, Medal> getMedalMap() {
        return redisMapCache.getMap(MedalConstants.MEDAL_MAP_CACHE, Long.class, Medal.class).orElseGet(() -> {
            Map<Long, Medal> longMedalMap = medalService.list().stream().collect(Collectors.toMap(
                    Medal::getId,
                    medal -> medal,
                    (oldData, newData) -> newData
            ));
            redisMapCache.init(MedalConstants.MEDAL_MAP_CACHE, longMedalMap, MedalConstants.MEDAL_MAP_TIMEOUT, MedalConstants.MEDAL_MAP_TIMEUNIT);
            return longMedalMap;
        });
    }

    public Map<Long, UserMedalVO> getGrepMap() {
        return getMedalMap().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> MedalConverter.INSTANCE.medalToUserMedalVO(entry.getValue()),
                (oldData, newData) -> newData
        ));
    }

    public Medal get(Long medalId) {
        return getMedalMap().get(medalId);
    }

    public boolean containsKey(Long medalId) {
        return getMedalMap().containsKey(medalId);
    }
}
