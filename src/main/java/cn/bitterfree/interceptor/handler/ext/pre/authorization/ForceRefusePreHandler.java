package cn.bitterfree.interceptor.handler.ext.pre.authorization;

import cn.bitterfree.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.common.exception.GlobalServiceException;
import cn.bitterfree.interceptor.config.ForceRefuseUrlsConfig;
import cn.bitterfree.interceptor.handler.InterceptorHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 20:48
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ForceRefusePreHandler extends InterceptorHandler {

    public final ForceRefuseUrlsConfig forceRefuseUrlsConfig;

    @Override
    public List<String> pathPatterns() {
        return forceRefuseUrlsConfig.getUrls();
    }

    @Override
    public void action(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.warn("强制拦截");
        throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_AUTHORIZED);
    }
}
