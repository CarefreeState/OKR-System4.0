package cn.bitterfree.domain.medal.service.impl;


import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.domain.medal.model.converter.UserMedalConverter;
import cn.bitterfree.domain.medal.model.entity.Medal;
import cn.bitterfree.domain.medal.model.entity.UserMedal;
import cn.bitterfree.domain.medal.model.mapper.UserMedalMapper;
import cn.bitterfree.domain.medal.model.vo.UserMedalVO;
import cn.bitterfree.domain.medal.repository.MedalMap;
import cn.bitterfree.domain.medal.service.UserMedalService;
import cn.bitterfree.domain.medal.util.MedalUtil;
import cn.bitterfree.redis.cache.RedisCache;
import cn.bitterfree.redis.cache.RedisMapCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.bitterfree.domain.medal.constants.MedalConstants.*;

/**
* @author 马拉圈
* @description 针对表【user_medal(用户勋章关联表)】的数据库操作Service实现
* @createDate 2024-04-07 11:36:52
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class UserMedalServiceImpl extends ServiceImpl<UserMedalMapper, UserMedal>
    implements UserMedalService {


    private final RedisCache redisCache;

    private final RedisMapCache redisMapCache;

    private final MedalMap medalMap;

    @Override
    public Map<Long, UserMedal> getUserMedalMap(Long userId) {
        String redisKey = USER_MEDAL_MAP_CACHE + userId;
        Map<Long, UserMedal> longUserMedalMap = redisMapCache.getMap(redisKey, Long.class, UserMedal.class).orElseGet(() -> {
            Map<Long, UserMedal> userMedalMap = this.lambdaQuery().eq(UserMedal::getUserId, userId)
                    .isNotNull(UserMedal::getIssueTime)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(
                            UserMedal::getMedalId,
                            userMedal -> userMedal,
                            (oldData, newData) -> newData
                    ));
            redisMapCache.putAllOver(redisKey, userMedalMap);
            return userMedalMap;
        });
        // 刷新缓存时间
        redisCache.expire(redisKey, USER_MEDAL_MAP_TIMEOUT, USER_MEDAL_MAP_TIMEUNIT);
        return longUserMedalMap;
    }

    @Override
    public void saveUserMedal(Long userId, Long medalId, UserMedal dbUserMedal, Long newCredit, Integer coefficient) {
        String redisKey = USER_MEDAL_MAP_CACHE + userId;
        if(!medalMap.containsKey(medalId)) {
            return;
        }
        String medalName = medalMap.get(medalId).getName();
        Integer level = MedalUtil.getLevel(newCredit, coefficient);
        // 1. 获取用户的徽章
        Date now = new Date();
        Optional.ofNullable(dbUserMedal).ifPresentOrElse(userMedal -> {
            // 更新积分，判断是否更新等级，如果更新等级则标记为未读（新的一次颁布）
            userMedal.setCredit(newCredit);
            // 只升级不降级
            if(userMedal.getLevel().compareTo(level) < 0) {
                userMedal.setLevel(level);
                userMedal.setIsRead(Boolean.FALSE);
                userMedal.setUpdateTime(now);
                userMedal.setIssueTime(now);
                log.info("颁布勋章 {} {} 等级 {} -> 用户 {} ", medalId, medalName, level, newCredit);
            }
            redisMapCache.put(redisKey, medalId, userMedal);
        }, () -> {
            // 插入新的
            UserMedal medal = new UserMedal();
            medal.setMedalId(medalId);
            medal.setCredit(newCredit);
            medal.setUserId(userId);
            medal.setLevel(level);
            medal.setIsRead(Boolean.FALSE);
            if(level.compareTo(0) > 0) {
                medal.setCreateTime(now);
                medal.setIssueTime(now);
                log.info("颁布勋章 {} {} 等级 {} -> 用户 {} ", medalId, medalName, level, userId);
            }
            redisMapCache.put(redisKey, medalId, medal);
        });
    }

    @Override
    public UserMedal getUserMedal(Long userId, Long medalId) {
        return getUserMedalMap(userId).get(medalId);
    }

    @Override
    public List<UserMedalVO> getUserMedalListAll(Long userId) {
        // 获取灰色
        Map<Long, UserMedalVO> grepMap = medalMap.getGrepMap();
        getUserMedalMap(userId).values()
                .stream()
                .filter(userMedal -> Objects.nonNull(userMedal.getLevel()) && userMedal.getLevel().compareTo(0) > 0)
                .forEach(userMedal -> {
                    Long medalId = userMedal.getMedalId();
                    UserMedalVO userMedalVO = grepMap.get(medalId);
                    UserMedalConverter.INSTANCE.userMedalMapToUserMedalVO(userMedal, userMedalVO);
                    userMedalVO.setUrl(medalMap.get(medalId).getUrl());
                });
        return grepMap.values().stream()
                .sorted(Comparator.comparing(UserMedalVO::getMedalId))
                .toList();
    }

    @Override
    public List<UserMedalVO> getUserMedalListUnread(Long userId) {
        return getUserMedalMap(userId).values()
                .stream()
                .filter(userMedal -> Objects.nonNull(userMedal.getLevel()) && userMedal.getLevel().compareTo(0) > 0)
                .filter(userMedal -> !userMedal.getIsRead())
                .map(userMedal -> {
                    UserMedalVO userMedalVO = UserMedalConverter.INSTANCE.userMedalToUserMedalVO(userMedal);
                    Medal medal = medalMap.get(userMedal.getMedalId());
                    UserMedalConverter.INSTANCE.medalMapToUserMedalVO(medal, userMedalVO);
                    return userMedalVO;
                })
                .sorted(Comparator.comparing(UserMedalVO::getMedalId))
                .collect(Collectors.toList());
    }

    @Override
    public void readUserMedal(Long userId, Long medalId) {
        if (!medalMap.containsKey(medalId)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.MEDAL_NOT_EXISTS);
        }
        Optional.ofNullable(getUserMedal(userId, medalId)).ifPresent(userMedal -> {
            userMedal.setIsRead(Boolean.TRUE);
            redisMapCache.put(USER_MEDAL_MAP_CACHE + userId, medalId, userMedal);
        });
    }
}




