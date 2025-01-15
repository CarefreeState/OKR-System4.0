package cn.lbcmmszdntnt.interceptor.handler;

import cn.lbcmmszdntnt.common.util.web.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 20:10
 */
public abstract class InterceptorHandler {

    private final static List<String> DEFAULT_PATH_PATTERNS = List.of("/**");

    private InterceptorHandler next;
    private InterceptorHandler prev;

    public List<String> pathPatterns() {
        return DEFAULT_PATH_PATTERNS;
    };

    public Boolean condition() {
        return Boolean.TRUE;
    }

    private void doNext(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Optional.ofNullable(this.next).ifPresent(nextHandler -> {
            nextHandler.handle(request, response, handler);
        });
    }

    public abstract void action(HttpServletRequest request, HttpServletResponse response, Object handler);

    public final void handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if(HttpRequestUtil.anyMatchPath(pathPatterns(), uri) && Boolean.TRUE.equals(condition())) {
            action(request, response, handler);
        }
        doNext(request, response, handler);
    }

    private void setNext(InterceptorHandler next) {
        this.next = next;
    }

    private void setPrev(InterceptorHandler prev) {
        this.prev = prev;
    }

    // 添加处理器在目标处理器之前
    public static void addHandlerBefore(InterceptorHandler beforeHandler, InterceptorHandler targetHandler) {
        Optional.ofNullable(targetHandler.prev).ifPresent(prevHandler -> {
            prevHandler.setNext(beforeHandler);
            beforeHandler.setPrev(prevHandler);
        });
        beforeHandler.setNext(targetHandler);
        targetHandler.setPrev(beforeHandler);
    }

    // 添加处理器在目标处理器之后
    public static void addHandlerAfter(InterceptorHandler afterHandler, InterceptorHandler targetHandler) {
        Optional.ofNullable(targetHandler.next).ifPresent(nextHandler -> {
            nextHandler.setPrev(afterHandler);
            afterHandler.setNext(nextHandler);
        });
        afterHandler.setPrev(targetHandler);
        targetHandler.setNext(afterHandler);
    }

}
