package cn.lbcmmszdntnt.interceptor.context;

import cn.lbcmmszdntnt.common.util.thread.local.ThreadLocalMapUtil;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.interceptor.annotation.Intercept;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-06
 * Time: 19:58
 */
public class InterceptorContext {

    private final static String USER = "user";
    private final static String INTERCEPT = "intercept";
    private final static String IS_AUTHENTICATED = "isAuthenticated";
    private final static String IS_AUTHORIZED = "isAuthorized";

    public static User getUser() {
        return ThreadLocalMapUtil.get(USER, User.class);
    }

    public static void setUser(User user) {
        ThreadLocalMapUtil.set(USER, user);
    }

    public static void setIntercept(Intercept intercept) {
        ThreadLocalMapUtil.set(INTERCEPT, intercept);
    }

    public static Intercept getIntercept() {
        return ThreadLocalMapUtil.get(INTERCEPT, Intercept.class);
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
