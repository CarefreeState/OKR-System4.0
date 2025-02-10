package cn.bitterfree.interceptor.handler.ext.after;

import cn.bitterfree.common.util.juc.treadlocal.ThreadLocalMapUtil;
import cn.bitterfree.interceptor.handler.InterceptorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 20:49
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContextClearAfterHandler extends InterceptorHandler {

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("删除本地线程变量以及日志变量");
        ThreadLocalMapUtil.removeAll();
        MDC.clear();
    }
}
