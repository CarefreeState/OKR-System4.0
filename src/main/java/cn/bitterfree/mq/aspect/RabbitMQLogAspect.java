package cn.bitterfree.mq.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-16
 * Time: 10:37
 */
@Component
@Aspect
@Slf4j
public class RabbitMQLogAspect {

    @Pointcut("execution(* cn.bitterfree..*.*(..)) && @annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public void receiveMessage() {}

    @Before("receiveMessage()")
    public void logMessage(JoinPoint joinPoint) {
        log.info("当前服务器收到来自 RabbitMQ 的消息 {}", joinPoint.getArgs());
    }

}
