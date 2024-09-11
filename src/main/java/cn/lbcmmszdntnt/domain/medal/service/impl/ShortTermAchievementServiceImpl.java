package cn.lbcmmszdntnt.domain.medal.service.impl;


import cn.lbcmmszdntnt.domain.medal.handler.chain.MedalHandlerChain;
import cn.lbcmmszdntnt.domain.medal.model.entry.ShortTermAchievement;
import cn.lbcmmszdntnt.domain.medal.service.TermAchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 23:15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortTermAchievementServiceImpl implements TermAchievementService {

    private final MedalHandlerChain medalHandlerChain;

    @Override
    public void issueTermAchievement(Long userId, Boolean isCompleted, Boolean oldCompleted) {
        ShortTermAchievement shortTermAchievement = ShortTermAchievement.builder()
                .userId(userId).isCompleted(isCompleted).oldCompleted(oldCompleted).build();
        medalHandlerChain.handle(shortTermAchievement);
    }

}