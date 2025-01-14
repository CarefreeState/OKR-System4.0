package cn.lbcmmszdntnt.email.aspect;

import cn.lbcmmszdntnt.common.util.thread.pool.ThreadPoolUtil;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-15
 * Time: 15:20
 */
@Component
@Aspect
@Slf4j
public class EmailAsynchronousSendAspect {

    private static class EmailAsynchronousThreadPool {

        private final static String THREAD_NAME = "Email-Thread";

        private final static ThreadPoolExecutor EMAIL_ASYNCHRONOUS_THREAD_POOL;

        static {
            EMAIL_ASYNCHRONOUS_THREAD_POOL = ThreadPoolUtil.getIoTargetThreadPool(THREAD_NAME);
        }

        private static void submit(Runnable... tasks) {
            Arrays.stream(tasks).forEach(EmailAsynchronousThreadPool::submit);
        }

        private static void submit(Runnable runnable) {
            EMAIL_ASYNCHRONOUS_THREAD_POOL.submit(runnable);
        }

    }

    @Pointcut("execution(* cn.lbcmmszdntnt.email.sender.EmailSender.*(..))")
    public void send() {}

    @Around("send()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        // 如果是自调用则不会触发切点
        EmailAsynchronousThreadPool.submit(() -> {
            try {
                log.info("异步发送邮件");
                joinPoint.proceed();
            } catch (Throwable e) {
                throw new GlobalServiceException(e.getMessage());
            }
        });
        return null;
    }

}
