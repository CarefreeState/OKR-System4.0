package cn.bitterfree.interceptor.context;

import cn.bitterfree.common.util.juc.treadlocal.ThreadLocalMapUtil;
import cn.bitterfree.domain.user.model.entity.User;
import cn.bitterfree.interceptor.config.InterceptProperties;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 19:58
 */
public class InterceptorContext {

    private final static String USER = "user";
    private final static String JWT = "jwt";
    private final static String INTERCEPT_PROPERTIES = "interceptProperties";

    private final static String IS_AUTHENTICATED = "isAuthenticated";
    private final static String IS_AUTHORIZED = "isAuthorized";

    public static User getUser() {
        return ThreadLocalMapUtil.get(USER, User.class);
    }

    public static void setUser(User user) {
        ThreadLocalMapUtil.set(USER, user);
    }

    public static String getJwt() {
        return ThreadLocalMapUtil.get(JWT, String.class);
    }

    public static void setJwt(String jwt) {
        ThreadLocalMapUtil.set(JWT, jwt);
    }

    public static void setInterceptProperties(InterceptProperties intercept) {
        ThreadLocalMapUtil.set(INTERCEPT_PROPERTIES, intercept);
    }

    public static InterceptProperties getInterceptProperties() {
        return ThreadLocalMapUtil.get(INTERCEPT_PROPERTIES, InterceptProperties.class);
    }

    public static void setIsAuthenticated(Boolean isAuthenticated) {
        ThreadLocalMapUtil.set(IS_AUTHENTICATED, isAuthenticated);
    }

    public static boolean isAuthenticated() {
        return Boolean.TRUE.equals(ThreadLocalMapUtil.get(IS_AUTHENTICATED, Boolean.class));
    }

    public static void setIsAuthorized(Boolean isAuthorized) {
        ThreadLocalMapUtil.set(IS_AUTHORIZED, isAuthorized);
    }

    public static boolean isAuthorized() {
        return Boolean.TRUE.equals(ThreadLocalMapUtil.get(IS_AUTHORIZED, Boolean.class));
    }

}
