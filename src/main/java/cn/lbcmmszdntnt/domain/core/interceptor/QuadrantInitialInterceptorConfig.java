package cn.lbcmmszdntnt.domain.core.interceptor;

import cn.lbcmmszdntnt.domain.core.interceptor.QuadrantInitialAfterHandler;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
import cn.lbcmmszdntnt.interceptor.handler.ext.after.ContextClearAfterHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

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


    private final QuadrantInitialAfterHandler quadrantInitialAfterHandler;
    private final ContextClearAfterHandler contextClearAfterHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        InterceptorHandler.addHandlerBefore(quadrantInitialAfterHandler, contextClearAfterHandler);
    }
}
