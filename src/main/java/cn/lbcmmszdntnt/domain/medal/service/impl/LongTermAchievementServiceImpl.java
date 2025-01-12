package cn.lbcmmszdntnt.domain.medal.service.impl;


import cn.lbcmmszdntnt.domain.medal.enums.MedalType;
import cn.lbcmmszdntnt.domain.medal.service.TermAchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 23:14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LongTermAchievementServiceImpl implements TermAchievementService {

    @Override
    public MedalType getMedalType() {
        return MedalType.LONG_TERM_ACHIEVEMENT;
    }

}
