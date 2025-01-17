package cn.lbcmmszdntnt.domain.coredeadline.listener;

import cn.lbcmmszdntnt.domain.core.constants.DelayExchangeConstants;
import cn.lbcmmszdntnt.domain.core.model.entity.quadrant.SecondQuadrant;
import cn.lbcmmszdntnt.domain.core.model.entity.quadrant.ThirdQuadrant;
import cn.lbcmmszdntnt.domain.core.model.message.deadline.FirstQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.message.deadline.SecondQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.message.deadline.ThirdQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.SecondQuadrantService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.ThirdQuadrantService;
import cn.lbcmmszdntnt.domain.core.util.QuadrantDeadlineMessageUtil;
import cn.lbcmmszdntnt.domain.coredeadline.constants.CoreDeadlineConstants;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-10
 * Time: 14:12
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QuadrantDeadlineMessageListener {

    private final RedisLock redisLock;

    private final OkrCoreService okrCoreService;

    private final SecondQuadrantService secondQuadrantService;

    private final ThirdQuadrantService thirdQuadrantService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = DelayExchangeConstants.FIRST_QUADRANT_DDL_QUEUE),
            exchange = @Exchange(name = DelayExchangeConstants.QUADRANT_DDL_DELAY_DIRECT, delayed = "true"),
            key = DelayExchangeConstants.FIRST_DDL
    ))
    // 不存在会创建（如果 start-up 为 true），但是只有名字的 queue，可以设置 queuesToDeclare，这个可以更细致的声明 queue，设置 bindings 则更细致...
    public void firstQuadrantDeadlineListener(FirstQuadrantEvent firstQuadrantEvent) {
        Long id = firstQuadrantEvent.getCoreId();
        Date firstQuadrantDeadline = firstQuadrantEvent.getDeadline();
        long nowTimestamp = System.currentTimeMillis();
        log.info("处理事件：内核 ID {}，第一象限截止时间 {}", id, firstQuadrantDeadline);
        // 1. 判断是否截止
        if(Objects.nonNull(firstQuadrantDeadline) &&
                firstQuadrantDeadline.getTime() <= nowTimestamp) {
            okrCoreService.complete(id);
            return;
        }
        // 2. 是否设置了第一象限截止时间（这里一定代表未截止）
        if(Objects.nonNull(firstQuadrantDeadline)) {
            QuadrantDeadlineMessageUtil.scheduledComplete(firstQuadrantEvent);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = DelayExchangeConstants.SECOND_QUADRANT_DDL_QUEUE),
            exchange = @Exchange(name = DelayExchangeConstants.QUADRANT_DDL_DELAY_DIRECT, delayed = "true"),
            key = DelayExchangeConstants.SECOND_DDL
    ))
    public void secondQuadrantDeadlineListener(SecondQuadrantEvent secondQuadrantEvent) {
        long nowTimestamp = System.currentTimeMillis();
        Long id = secondQuadrantEvent.getCoreId();
        Long secondQuadrantId = secondQuadrantEvent.getId();
        Integer secondQuadrantCycle = secondQuadrantEvent.getCycle();
        // 判断消息是否有效需要执行（判断数据库的 deadline 与此消息的 deadline 是否一致）
        // （若不一致，则代表此消息已无效，无需重发，因为认定数据库里的 deadline 也有自己的消息，若没有，则代表消息丢失，再此弥补也无济于事）
        Date secondQuadrantDeadline = secondQuadrantEvent.getDeadline();
        long deadTimestamp = secondQuadrantDeadline.getTime();
        redisLock.tryLockDoSomething(CoreDeadlineConstants.SECOND_QUADRANT_DEADLINE_LOCK + secondQuadrantId, () -> {
            secondQuadrantService.lambdaQuery().eq(SecondQuadrant::getId, secondQuadrantId).oneOpt().map(SecondQuadrant::getDeadline).map(Date::getTime).ifPresent(dbDeadline -> {
                if(!dbDeadline.equals(deadTimestamp)) {
                    return;
                }
                log.info("处理事件：内核 ID {}，第二象限 ID {}，第二象限截止时间 {}，第二象限周期 {}",
                        id, secondQuadrantId, secondQuadrantDeadline, secondQuadrantCycle);
                // 是否设置了第二象限截止时间和周期
                if(Objects.nonNull(secondQuadrantCycle)) {
                    // 判断是否结束
                    Boolean isOver = okrCoreService.getOkrCore(id).getIsOver();
                    if(Boolean.TRUE.equals(isOver)) {
                        log.warn("OKR {} 已结束，第二象限 {} 停止对截止时间的刷新", id, secondQuadrantId);
                        return;
                    }
                    // 1. 获取一个正确的截止点
                    long nextDeadTimestamp = deadTimestamp;
                    final long cycle = TimeUnit.SECONDS.toMillis(secondQuadrantCycle);
                    while(nextDeadTimestamp <= nowTimestamp) {
                        nextDeadTimestamp += cycle;
                    }
                    Date nextDeadline = new Date(nextDeadTimestamp);
                    // 2. 更新截止时间
                    if(nextDeadTimestamp != deadTimestamp) {
                        secondQuadrantService.updateDeadline(secondQuadrantId, nextDeadline);
                    }
                    // 3. 发起延时任务
                    secondQuadrantEvent.setDeadline(nextDeadline);
                    QuadrantDeadlineMessageUtil.scheduledUpdateSecondQuadrant(secondQuadrantEvent);
                }
            });
        }, () -> {});
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = DelayExchangeConstants.THIRD_QUADRANT_DDL_QUEUE),
            exchange = @Exchange(name = DelayExchangeConstants.QUADRANT_DDL_DELAY_DIRECT, delayed = "true"),
            key = DelayExchangeConstants.THIRD_DDL
    ))
    public void thirdQuadrantDeadlineListener(ThirdQuadrantEvent thirdQuadrantEvent) {
        long nowTimestamp = System.currentTimeMillis();
        Long id = thirdQuadrantEvent.getCoreId();
        Long thirdQuadrantId = thirdQuadrantEvent.getId();
        Integer thirdQuadrantCycle = thirdQuadrantEvent.getCycle();
        // 判断消息是否有效需要执行（判断数据库的 deadline 与此消息的 deadline 是否一致）
        // （若不一致，则代表此消息已无效，无需重发，因为认定数据库里的 deadline 也有自己的消息，若没有，则代表消息丢失，再此弥补也无济于事）
        Date thirdQuadrantDeadline = thirdQuadrantEvent.getDeadline();
        long deadTimestamp = thirdQuadrantDeadline.getTime();
        redisLock.tryLockDoSomething(CoreDeadlineConstants.THIRD_QUADRANT_DEADLINE_LOCK + thirdQuadrantId, () -> {
            thirdQuadrantService.lambdaQuery().eq(ThirdQuadrant::getId, thirdQuadrantId).oneOpt().map(ThirdQuadrant::getDeadline).map(Date::getTime).ifPresent(dbDeadline -> {
                if(!dbDeadline.equals(deadTimestamp)) {
                    return;
                }
                log.info("处理事件：内核 ID {}，第三象限 ID {}，第三象限截止时间 {}，第三象限周期 {}",
                        id, thirdQuadrantId, thirdQuadrantDeadline, thirdQuadrantCycle);
                // 是否设置了第三象限截止时间和周期
                if(Objects.nonNull(thirdQuadrantCycle)) {
                    // 判断是否结束
                    Boolean isOver = okrCoreService.getOkrCore(id).getIsOver();
                    if(Boolean.TRUE.equals(isOver)) {
                        log.warn("OKR {} 已结束，第三象限 {} 停止对截止时间的刷新", id, thirdQuadrantId);
                        return;
                    }
                    // 1. 获取一个正确的截止点
                    long nextDeadTimestamp = deadTimestamp;
                    final long cycle = TimeUnit.SECONDS.toMillis(thirdQuadrantCycle);
                    while(nextDeadTimestamp <= nowTimestamp) {
                        nextDeadTimestamp += cycle;
                    }
                    Date nextDeadline = new Date(nextDeadTimestamp);
                    // 2. 更新截止时间
                    if(nextDeadTimestamp != deadTimestamp) {
                        thirdQuadrantService.updateDeadline(thirdQuadrantId, nextDeadline);
                    }
                    // 3. 发起延时任务
                    thirdQuadrantEvent.setDeadline(nextDeadline);
                    QuadrantDeadlineMessageUtil.scheduledUpdateThirdQuadrant(thirdQuadrantEvent);
                }
            });

        }, () -> {});
    }

}
