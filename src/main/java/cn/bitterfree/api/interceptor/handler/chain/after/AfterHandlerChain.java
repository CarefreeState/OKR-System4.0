package cn.bitterfree.api.interceptor.handler.chain.after;

import cn.bitterfree.api.interceptor.handler.InterceptorHandler;
import cn.bitterfree.api.interceptor.handler.ext.after.ContextClearAfterHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 20:56
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AfterHandlerChain extends InterceptorHandler implements InitializingBean {

    private final ContextClearAfterHandler contextClearAfterHandler;

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("后置责任链开始执行");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        InterceptorHandler.addHandlerAfter(contextClearAfterHandler, this);
    }
}
