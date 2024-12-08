package cn.lbcmmszdntnt.domain.medal.service.impl;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.medal.config.properties.MedalList;
import cn.lbcmmszdntnt.domain.medal.config.properties.MedalMap;
import cn.lbcmmszdntnt.domain.medal.model.converter.UserMalConverter;
import cn.lbcmmszdntnt.domain.medal.model.mapper.UserMedalMapper;
import cn.lbcmmszdntnt.domain.medal.model.po.Medal;
import cn.lbcmmszdntnt.domain.medal.model.po.UserMedal;
import cn.lbcmmszdntnt.domain.medal.model.vo.UserMedalVO;
import cn.lbcmmszdntnt.domain.medal.service.UserMedalService;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.redis.cache.RedisCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 马拉圈
* @description 针对表【user_medal(用户勋章关联表)】的数据库操作Service实现
* @createDate 2024-04-07 11:36:52
*/
@Service
@RequiredArgsConstructor
public class UserMedalServiceImpl extends ServiceImpl<UserMedalMapper, UserMedal>
    implements UserMedalService {

    private final static String USER_MEDAL_ID_MAP = "userMedalIdMap:%d:%d";

    private final static Long USER_MEDAL_ID_TTL = 1L;

    private final static TimeUnit USER_MEDAL_ID_UNIT = TimeUnit.DAYS;

    private final RedisCache redisCache;

    private final MedalMap medalMap;

    private final MedalList medalList;

    @Override
    public UserMedal getUserMedal(Long userId, Long medalId) {
        String redisKey = String.format(USER_MEDAL_ID_MAP, userId, medalId);
        Boolean exists = redisCache.isExists(redisKey);
        if(Boolean.TRUE.equals(exists)) {
            return redisCache.getObject(redisKey, UserMedal.class).orElse(null);
        } else {
            UserMedal userMedal = this.lambdaQuery()
                    .eq(UserMedal::getUserId, userId)
                    .eq(UserMedal::getMedalId, medalId).one();
            redisCache.setObject(redisKey, userMedal, USER_MEDAL_ID_TTL, USER_MEDAL_ID_UNIT);
            return userMedal;
        }
    }

    @Override
    public void deleteDbUserMedalCache(Long userId, Long medalId) {
        String redisKey = String.format(USER_MEDAL_ID_MAP, userId, medalId);
        redisCache.deleteObject(redisKey);
    }

    private UserMedalVO userMedalMap(UserMedal userMedal) {
        UserMedalVO userMedalVO = UserMalConverter.INSTANCE.userMedalToUserMedalVO(userMedal);
        Medal medal = medalMap.get(userMedal.getMedalId());
        UserMalConverter.INSTANCE.medalMapToUserMedalVO(medal, userMedalVO);
        return userMedalVO;
    }

    @Override
    public List<UserMedalVO> getUserMedalListAll(Long userId) {
        // 获取灰色
        List<UserMedalVO> grepList = medalList.getGrepList();
        this.lambdaQuery().eq(UserMedal::getUserId, userId)
                .ne(UserMedal::getLevel, 0).isNotNull(UserMedal::getIssueTime)
                .list()
                .forEach(userMedal -> {
                    Long medalId = userMedal.getMedalId();
                    int index = (int) (medalId - 1);
                    UserMedalVO userMedalVO = grepList.get(index);
                    UserMalConverter.INSTANCE.userMedalMapToUserMedalVO(userMedal, userMedalVO);
                    userMedalVO.setUrl(medalMap.get(medalId).getUrl());
                });
        return grepList;
    }

    @Override
    public List<UserMedalVO> getUserMedalListUnread(Long userId) {
        return this.lambdaQuery().eq(UserMedal::getUserId, userId)
                .ne(UserMedal::getLevel, 0).isNotNull(UserMedal::getIssueTime).eq(UserMedal::getIsRead, Boolean.FALSE)
                .list().stream().parallel()
                .map(this::userMedalMap)
                .sorted(Comparator.comparing(UserMedalVO::getMedalId))
                .collect(Collectors.toList());
    }

    @Override
    public void readUserMedal(Long userId, Long medalId) {
        if (!medalMap.containsKey(medalId)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.MEDAL_NOT_EXISTS);
        }
        this.lambdaUpdate()
                .eq(UserMedal::getUserId, userId)
                .eq(UserMedal::getMedalId, medalId)
                .eq(UserMedal::getIsRead, Boolean.FALSE)
                .set(UserMedal::getIsRead, Boolean.TRUE)
                .update();
    }
}




