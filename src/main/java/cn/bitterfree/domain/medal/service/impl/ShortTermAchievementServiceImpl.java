package cn.bitterfree.domain.medal.service.impl;


import cn.bitterfree.domain.medal.enums.MedalType;
import cn.bitterfree.domain.medal.service.TermAchievementService;
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

    @Override
    public MedalType getMedalType() {
        return MedalType.SHORT_TERM_ACHIEVEMENT;
    }

}
