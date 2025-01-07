package cn.lbcmmszdntnt.domain.core.config;

import cn.lbcmmszdntnt.aop.config.AfterInterceptConfig;
import cn.lbcmmszdntnt.common.util.thread.local.ThreadLocalMapUtil;
import cn.lbcmmszdntnt.common.util.thread.pool.IOThreadPool;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.medal.handler.chain.MedalHandlerChain;
import cn.lbcmmszdntnt.domain.medal.model.entity.UserMedal;
import cn.lbcmmszdntnt.domain.medal.model.entity.entry.StayTrueBeginning;
import cn.lbcmmszdntnt.domain.medal.service.UserMedalService;
import cn.lbcmmszdntnt.domain.user.util.UserRecordUtil;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import cn.lbcmmszdntnt.interceptor.handler.ext.after.ContextClearAfterHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-07
 * Time: 2:26
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class QuadrantInitialInterceptorConfig implements InitializingBean {

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

    private final ContextClearAfterHandler contextClearAfterHandler;

    @Bean
    public InterceptorHandler quadrantInitialAfterHandler() {
        return new InterceptorHandler() {

            @Override
            public List<String> pathPatterns() {
                return QUADRANT_INITIAL_PATH_PATTERNS;
            }

            @Override
            public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
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
        };

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        InterceptorHandler.addHandlerBefore(quadrantInitialAfterHandler(), contextClearAfterHandler);
    }
}
