package cn.lbcmmszdntnt.interceptor.handler;

import cn.lbcmmszdntnt.common.util.web.HttpUtil;
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

    protected void doNext(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Optional.ofNullable(this.next).ifPresent(nextHandler -> {
            nextHandler.handle(request, response, handler);
        });
    }

    public abstract void action(HttpServletRequest request, HttpServletResponse response, Object handler);
    public final void handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if(HttpUtil.anyMatchPath(pathPatterns(), uri) && Boolean.TRUE.equals(condition())) {
            action(request, response, handler);
        }
        doNext(request, response, handler);
    }

    protected void setNext(InterceptorHandler next) {
        this.next = next;
    }

    protected void setPrev(InterceptorHandler prev) {
        this.prev = prev;
    }

    public static void addHandlerBefore(InterceptorHandler handler, InterceptorHandler beforeHandler) {
        Optional.ofNullable(beforeHandler.prev).ifPresent(prevHandler -> {
            prevHandler.setNext(handler);
            handler.setPrev(prevHandler);
        });
        handler.setNext(beforeHandler);
        beforeHandler.setPrev(handler);
    }

    public static void addHandlerAfter(InterceptorHandler handler, InterceptorHandler afterHandler) {
        Optional.ofNullable(afterHandler.next).ifPresent(nextHandler -> {
            nextHandler.setPrev(handler);
            handler.setNext(nextHandler);
        });
        handler.setPrev(afterHandler);
        afterHandler.setNext(handler);
    }

}
