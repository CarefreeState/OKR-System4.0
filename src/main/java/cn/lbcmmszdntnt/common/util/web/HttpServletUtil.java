package cn.lbcmmszdntnt.common.util.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 3:42
 */
public class HttpServletUtil {

    @Nullable
    public static ServletRequestAttributes getAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    @Nullable
    public static HttpServletRequest getRequest() {
        return Optional.ofNullable(getAttributes()).map(ServletRequestAttributes::getRequest).orElse(null);
    }

    @Nullable
    public static HttpServletResponse getResponse() {
        return Optional.ofNullable(getAttributes()).map(ServletRequestAttributes::getResponse).orElse(null);
    }

}
