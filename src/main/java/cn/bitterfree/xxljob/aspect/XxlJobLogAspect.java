package cn.bitterfree.xxljob.aspect;

import cn.bitterfree.xxljob.annotation.XxlRegister;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-16
 * Time: 10:07
 */
@Component
@Aspect
@Slf4j
public class XxlJobLogAspect {

    @Pointcut("execution(* cn.bitterfree..*.*(..)) && @annotation(cn.bitterfree.xxljob.annotation.XxlRegister) && @annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void runJob() {}

    @Before("runJob()") // ProceedingJoinPoint 不支持 @Before，要用 JoinPoint
    public void logDescription(JoinPoint joinPoint) {
        if(joinPoint.getSignature() instanceof MethodSignature methodSignature) {
            XxlRegister annotation = methodSignature.getMethod().getAnnotation(XxlRegister.class);
            log.info("XXL-JOB 执行 {} [CRON {}, AUTHOR {}, ROUTE {}]", annotation.jobDesc(), annotation.cron(), annotation.author(), annotation.executorRouteStrategy());
        }
    }

}
