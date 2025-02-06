package cn.lbcmmszdntnt.sse.annotation;

import org.springframework.web.method.HandlerMethod;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-06
 * Time: 17:59
 */
public class SseRequestHelper {

    public static boolean isSseRequest(Object handler) {
        return Optional.ofNullable(handler)
                .filter(HandlerMethod.class::isInstance)
                .map(HandlerMethod.class::cast)
                .map(HandlerMethod::getMethod)
                .filter(method -> method.isAnnotationPresent(SseRequest.class))
                .isPresent();
    }

}
