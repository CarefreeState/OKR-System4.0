package cn.bitterfree.api.domain.medal.config;

import cn.bitterfree.api.common.util.juc.threadpool.IOThreadPool;
import cn.bitterfree.api.domain.medal.constants.MedalConstants;
import cn.bitterfree.api.domain.medal.model.entity.UserMedal;
import cn.bitterfree.api.domain.medal.service.UserMedalService;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.redis.cache.RedisMapCache;
import cn.bitterfree.api.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-13
 * Time: 3:13
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class UserMedalPageOutConfig {

    private final static String ROUTE = "ROUND";
    private final static int TRIGGER_STATUS = 1;
    private final static String CRON = "0 0 0 * * ? *"; // 每天 0 点

    private final RedisCache redisCache;

    private final RedisMapCache redisMapCache;

    private final UserMedalService userMedalService;

    // 渐入佳境勋章
    @XxlJob(value = "pageOutUserMedal")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE, triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】每天一次的用户勋章缓存写入数据库")
    @Transactional
    public void pageOutUserMedal() {
        List<UserMedal> userMedalUpdateList = new ArrayList<>();
        List<UserMedal> userMedalSaveList = new ArrayList<>();
        // 数据量很大，需要分批处理
        Set<String> keys = redisCache.getKeysByPrefix(MedalConstants.USER_MEDAL_MAP_CACHE);
        IOThreadPool.operateBatch(keys.stream().toList(), redisKeyList -> {
            redisKeyList.forEach(redisKey -> {
                redisMapCache.getMap(redisKey, Long.class, UserMedal.class)
                        .map(Map::entrySet)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(Map.Entry::getValue)
                        .forEach(userMedal -> {
                            Optional.ofNullable(userMedal.getId()).ifPresentOrElse(id -> {
                                log.info("{} update medal", redisKey);
                                userMedalUpdateList.add(userMedal);
                            }, () -> {
                                log.info("{} save medal", redisKey);
                                userMedalSaveList.add(userMedal);
                            });
                        });
            });
        });
//        redisCache.deleteObjects(keys); // 无需删除
        userMedalService.saveBatch(userMedalSaveList);
        userMedalService.updateBatchById(userMedalUpdateList);
        log.info("用户勋章已成功写入数据库，共 {} 条更新，{} 条新增", userMedalUpdateList.size(), userMedalSaveList.size());
    }

}
