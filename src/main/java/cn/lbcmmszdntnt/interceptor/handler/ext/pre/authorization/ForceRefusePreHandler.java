package cn.lbcmmszdntnt.interceptor.handler.ext.pre.authorization;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.interceptor.config.ForceRefuseUrlsConfig;
import cn.lbcmmszdntnt.interceptor.handler.InterceptorHandler;
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
        log.error("强制拦截 {}", request.getRequestURI());
        throw new GlobalServiceException(GlobalServiceStatusCode.USER_NO_AUTHORIZED);
    }
}
