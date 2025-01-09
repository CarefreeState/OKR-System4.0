package cn.lbcmmszdntnt.interceptor.annotation;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-08-08
 * Time: 17:32
 */
@Slf4j
public class InterceptHelper {

    public static Intercept getIntercept(Class<?> clazz) {
        // 类上的 Intercept 为初步结果
        return clazz.isAnnotationPresent(Intercept.class) ? clazz.getAnnotation(Intercept.class) : null;
    }

    public static Intercept getIntercept(Method targetMethod) {
        log.error("{}", targetMethod);
        // 获取目标方法所在的类
        Class<?> declaringClass = targetMethod.getDeclaringClass();
        log.error("{}", declaringClass);
        // 类上的 Intercept 为初步结果
        Intercept intercept = getIntercept(declaringClass);
        // 方法上的 Intercept 为最终结果
        return targetMethod.isAnnotationPresent(Intercept.class) ? targetMethod.getAnnotation(Intercept.class) : intercept;
    }

}
