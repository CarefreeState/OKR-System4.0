package cn.bitterfree.interceptor.handler.chain.pre;

import cn.bitterfree.interceptor.handler.InterceptorHandler;
import cn.bitterfree.interceptor.handler.ext.pre.init.CustomInitPreHandler;
import cn.bitterfree.interceptor.handler.ext.pre.init.HandlerMethodInitPreHandler;
import cn.bitterfree.interceptor.handler.ext.pre.init.LogInitPreHandler;
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
 * Date: 2025-01-09
 * Time: 15:41
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InitPreHandlerChain extends InterceptorHandler implements InitializingBean {

    private final LogInitPreHandler logInitPreHandler;
    private final CustomInitPreHandler customInitPreHandler;
    private final HandlerMethodInitPreHandler handlerMethodInitPreHandler;

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("前置初始化拦截参数责任链开始执行");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        InterceptorHandler.addHandlerAfter(logInitPreHandler, this);
        InterceptorHandler.addHandlerAfter(customInitPreHandler, logInitPreHandler);
        InterceptorHandler.addHandlerAfter(handlerMethodInitPreHandler, customInitPreHandler); // 局部优先
    }
}
