package cn.lbcmmszdntnt.interceptor.annotation;


import cn.lbcmmszdntnt.domain.user.enums.UserType;

import java.lang.annotation.*;

/**
 * Created With Intellij IDEA
 * User: 马拉圈
 * Date: 2024-08-08
 * Time: 12:50
 * <br />
 * permit: 用户必须是列表中的角色才能访问 <br />
 * authenticate: 用户需不需要通过认证 <br />
 * authorize: 用户需不需要通过授权 <br />
 * 注意： <br />
 * permit 若为 {}，则代表谁都不能访问，除非 authorize 为 false <br />
 * 1. 不需要认证、不需要授权 ✅ <br />
 * 2. 需要认证、需要授权 ✅ <br />
 * 3. 需要认证、不需要授权 ✅ <br />
 * 4. 不需要认证、需要授权 ❌（必然失败） <br />
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Intercept {

    UserType[] permit() default {UserType.NORMAL_USER};

    boolean authenticate() default true; // 默认需要认证

    boolean authorize() default true; // 默认需要授权

}
