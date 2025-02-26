package cn.bitterfree.api.domain.medal.handler.ext;

import cn.bitterfree.api.domain.medal.enums.MedalType;
import cn.bitterfree.api.domain.medal.model.entity.UserMedal;
import cn.bitterfree.api.domain.medal.service.UserMedalService;
import cn.bitterfree.api.domain.medal.util.MedalUtil;
import cn.bitterfree.api.domain.okr.handler.ext.OkrUserMergeHandler;
import cn.bitterfree.api.domain.userbinding.handler.UserMergeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cn.bitterfree.api.domain.medal.constants.MedalConstants.USER_MEDAL_MAP_CACHE;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-26
 * Time: 9:30
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserMedalMergeHandler extends UserMergeHandler implements InitializingBean {

    private final UserMedalService userMedalService;

    private final OkrUserMergeHandler okrUserMergeHandler;

    private UserMedal mergeUserMedal(MedalType medalType, UserMedal mainUsermedal, UserMedal userMedal) {
        if(Objects.isNull(mainUsermedal)) {
            return userMedal;
        }
        if(Objects.isNull(userMedal)) {
            return mainUsermedal;
        }
        // 合并分数
        Long newCredit = mainUsermedal.getCredit() + userMedal.getCredit();
        mainUsermedal.setCredit(newCredit);
        // 计算新的等级
        Integer level = MedalUtil.getLevel(newCredit, medalType.getCoefficient());
        if(level.compareTo(mainUsermedal.getLevel()) > 0) {
            mainUsermedal.setLevel(level);
        }
        mainUsermedal.setIsRead(Boolean.FALSE);
        mainUsermedal.setIssueTime(new Date());
        return mainUsermedal;
    }

    @Override
    @Transactional
    public List<String> handle(Long mainUserId, Long userId) {
        log.info("合并勋章数据 {} <- {}", mainUserId, userId);
        // 同一个用户，不必考虑并发问题（结合实际）
        Map<Long, UserMedal> userMedalMap = userMedalService.getUserMedalMap(userId);
        Map<Long, UserMedal> mainUserMedalMap = userMedalService.getUserMedalMap(mainUserId);
        List<UserMedal> userMedalList = new ArrayList<>();
        Arrays.stream(MedalType.values()).forEach(medalType -> {
            Long medalId = medalType.getMedalId();
            Optional.ofNullable(mergeUserMedal(medalType, userMedalMap.get(medalId), mainUserMedalMap.get(medalId))).ifPresent(userMedal -> {
                userMedal.setId(null);
                userMedal.setUserId(mainUserId);
                userMedalList.add(userMedal);
            });
        });
        // 删除原数据
        userMedalService.lambdaUpdate()
                .in(UserMedal::getUserId, List.of(mainUserId, userId))
                .remove();
        userMedalService.saveBatch(userMedalList);
        List<String> redisKeys = super.doNextHandler(mainUserId, userId);
        redisKeys.add(USER_MEDAL_MAP_CACHE + userId);
        redisKeys.add(USER_MEDAL_MAP_CACHE + mainUserId);
        return redisKeys;
    }

    @Override
    public void afterPropertiesSet() {
        UserMergeHandler.addHandlerAfter(this, okrUserMergeHandler);
    }
}
