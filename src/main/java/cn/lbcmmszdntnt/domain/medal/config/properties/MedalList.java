package cn.lbcmmszdntnt.domain.medal.config.properties;


import cn.lbcmmszdntnt.domain.medal.model.converter.MedalConverter;
import cn.lbcmmszdntnt.domain.medal.model.po.Medal;
import cn.lbcmmszdntnt.domain.medal.model.vo.UserMedalVO;
import cn.lbcmmszdntnt.domain.medal.service.MedalService;
import cn.lbcmmszdntnt.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-12
 * Time: 18:08
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MedalList {

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static int TRIGGER_STATUS = 0;

    private final static String CRON = "0 0/1 * * * ? *";

    private final List<Medal> medalList = new ArrayList<>();

    private final MedalService medalService;

    @PostConstruct
    public void doPostConstruct() {
        medalService.lambdaQuery().list().stream()
                .sorted(Comparator.comparing(Medal::getId))
                .forEach(medalList::add);
    }

    @XxlJob(value = "medalListCacheUpdate")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR, triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】更新 MedalList 的本地缓存")
    private void medalListCacheUpdate() {
        synchronized (medalList) {
            log.info("更新一次 MedalList 的本地缓存");
            medalList.clear();
            medalService.lambdaQuery().list().stream()
                    .sorted(Comparator.comparing(Medal::getId))
                    .forEach(medalList::add);
        }
    }

    public Medal get(int index) {
        Medal medal = null;
        synchronized (medalList) {
            medal = medalList.get(index);
        }
        return medal;
    }

    public List<UserMedalVO> getGrepList() {
        // clone 的一份
        List<UserMedalVO> userMedalVOS = null;
        synchronized (medalList) {
            userMedalVOS = medalList.stream()
                    .map(MedalConverter.INSTANCE::medalToUserMedalVO)
                    .sorted(Comparator.comparing(UserMedalVO::getMedalId))
                    .toList();
            return userMedalVOS;
        }
    }
}
