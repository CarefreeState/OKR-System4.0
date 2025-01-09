package cn.lbcmmszdntnt.domain.core.interceptor;

import cn.lbcmmszdntnt.common.util.thread.pool.IOThreadPool;
import cn.lbcmmszdntnt.domain.core.context.OkrCoreContext;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.medal.handler.chain.MedalHandlerChain;
import cn.lbcmmszdntnt.domain.medal.model.entity.UserMedal;
import cn.lbcmmszdntnt.domain.medal.model.entity.entry.StayTrueBeginning;
import cn.lbcmmszdntnt.domain.medal.service.UserMedalService;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class QuadrantInitialAfterHandler extends InterceptorHandler {

    private final static List<String> QUADRANT_INITIAL_PATH_PATTERNS = List.of(
            "/firstquadrant/init",
            "/secondquadrant/init",
            "/thirdquadrant/init"
    );

    @Value("${medal.stay-true-beginning.id}")
    private Long medalId;

    private final OkrCoreService okrCoreService;
    private final UserMedalService userMedalService;
    private final MedalHandlerChain medalHandlerChain;

    @Override
    public List<String> pathPatterns() {
        return QUADRANT_INITIAL_PATH_PATTERNS;
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 初心启航
        Long coreId = OkrCoreContext.getCoreId();
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