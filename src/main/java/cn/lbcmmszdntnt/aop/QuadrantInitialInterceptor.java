package cn.lbcmmszdntnt.aop;


import cn.lbcmmszdntnt.aop.config.AfterInterceptConfig;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.medal.handler.chain.MedalHandlerChain;
import cn.lbcmmszdntnt.domain.medal.model.entry.StayTrueBeginning;
import cn.lbcmmszdntnt.domain.medal.model.po.UserMedal;
import cn.lbcmmszdntnt.domain.medal.service.UserMedalService;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.util.thread.local.ThreadLocalMapUtil;
import cn.lbcmmszdntnt.util.thread.pool.IOThreadPool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-14
 * Time: 0:45
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuadrantInitialInterceptor implements HandlerInterceptor {

    @Value("${medal.stay-true-beginning.id}")
    private Long medalId;

    private final OkrCoreService okrCoreService;

    private final UserMedalService userMedalService;

    private final MedalHandlerChain medalHandlerChain;

    // 这里【抛异常】不会影响响应，（但是这个方法会影响响应速度）
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 初心启航
        Long coreId = ThreadLocalMapUtil.get(AfterInterceptConfig.CORE_ID, Long.class);
        Long userId = UserRecordUtil.getUserRecord().getId();
        okrCoreService.checkOverThrows(coreId);
        // 启动一个异步线程
        IOThreadPool.submit(() -> {
            UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
            if(Objects.isNull(dbUserMedal)) {
                StayTrueBeginning stayTrueBeginning = StayTrueBeginning.builder().userId(userId).coreId(coreId).build();
                medalHandlerChain.handle(stayTrueBeginning);
            }
        });
    }
}
