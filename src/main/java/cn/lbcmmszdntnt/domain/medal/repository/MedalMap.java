package cn.lbcmmszdntnt.domain.medal.repository;


import cn.lbcmmszdntnt.domain.medal.model.converter.MedalConverter;
import cn.lbcmmszdntnt.domain.medal.model.entity.Medal;
import cn.lbcmmszdntnt.domain.medal.model.vo.UserMedalVO;
import cn.lbcmmszdntnt.domain.medal.service.MedalService;
import cn.lbcmmszdntnt.redis.cache.RedisMapCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Collectors;

import static cn.lbcmmszdntnt.domain.medal.constants.MedalConstants.*;

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
        return redisMapCache.getMap(MEDAL_MAP_CACHE, Long.class, Medal.class).orElseGet(() -> {
            Map<Long, Medal> longMedalMap = medalService.list().stream().collect(Collectors.toMap(
                    Medal::getId,
                    medal -> medal,
                    (oldData, newData) -> newData
            ));
            redisMapCache.init(MEDAL_MAP_CACHE, longMedalMap, MEDAL_MAP_TIMEOUT, MEDAL_MAP_TIMEUNIT);
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
