package cn.lbcmmszdntnt.domain.coredeadline.listener;

import cn.lbcmmszdntnt.common.enums.EmailTemplate;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.common.exception.GlobalServiceException;
import cn.lbcmmszdntnt.domain.core.constants.DelayExchangeConstants;
import cn.lbcmmszdntnt.domain.core.model.message.deadline.FirstQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.message.deadline.SecondQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.message.deadline.ThirdQuadrantEvent;
import cn.lbcmmszdntnt.domain.core.model.vo.OkrCoreVO;
import cn.lbcmmszdntnt.domain.core.model.vo.quadrant.SecondQuadrantVO;
import cn.lbcmmszdntnt.domain.core.model.vo.quadrant.ThirdQuadrantVO;
import cn.lbcmmszdntnt.domain.core.service.OkrCoreService;
import cn.lbcmmszdntnt.domain.core.service.OkrOperateService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.SecondQuadrantService;
import cn.lbcmmszdntnt.domain.core.service.quadrant.ThirdQuadrantService;
import cn.lbcmmszdntnt.domain.core.util.QuadrantDeadlineMessageUtil;
import cn.lbcmmszdntnt.domain.coredeadline.constants.CoreDeadlineConstants;
import cn.lbcmmszdntnt.domain.coredeadline.util.DeadlineUtil;
import cn.lbcmmszdntnt.domain.user.model.entity.User;
import cn.lbcmmszdntnt.domain.user.service.UserService;
import cn.lbcmmszdntnt.redis.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    private final UserService userService;

    private final OkrCoreService okrCoreService;

    private final SecondQuadrantService secondQuadrantService;

    private final ThirdQuadrantService thirdQuadrantService;

    private final List<OkrOperateService> okrOperateServiceList;

    private User getUserByCoreId(Long coreId) {
        // 多次查询 userId，但没有关系，只要一轮就可以看是否命中缓存
        for (OkrOperateService service : okrOperateServiceList) {
            try {
                return userService.getUserById(service.getCoreUser(coreId)).orElse(null);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = DelayExchangeConstants.FIRST_QUADRANT_DDL_QUEUE),
            exchange = @Exchange(name = DelayExchangeConstants.QUADRANT_DDL_DELAY_DIRECT, delayed = "true"),
            key = DelayExchangeConstants.FIRST_DDL
    ))
    // 不存在会创建（如果 start-up 为 true），但是只有名字的 queue，可以设置 queuesToDeclare，这个可以更细致的声明 queue，设置 bindings 则更细致...
    public void firstQuadrantDeadlineListener(FirstQuadrantEvent firstQuadrantEvent) {
        Long coreId = firstQuadrantEvent.getCoreId();
        Date firstQuadrantDeadline = firstQuadrantEvent.getDeadline();
        long nowTimestamp = System.currentTimeMillis();
        log.info("处理事件：内核 ID {}，第一象限截止时间 {}", coreId, firstQuadrantDeadline);
        // 1. 判断是否截止
        if(firstQuadrantDeadline.getTime() <= nowTimestamp) {
            okrCoreService.complete(coreId);
            // 到这里未报错说明成功了（重复消费的话，其中一个到不了这里）
            okrCoreService.noticeOkr(
                    getUserByCoreId(coreId),
                    okrCoreService.searchOkrCore(coreId),
                    null,
                    EmailTemplate.OKR_ENDED_NOTICE
            );
            return;
        }
        // 2. 到这里一定代表未截止
        QuadrantDeadlineMessageUtil.scheduledComplete(firstQuadrantEvent);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = DelayExchangeConstants.SECOND_QUADRANT_DDL_QUEUE),
            exchange = @Exchange(name = DelayExchangeConstants.QUADRANT_DDL_DELAY_DIRECT, delayed = "true"),
            key = DelayExchangeConstants.SECOND_DDL
    ))
    public void secondQuadrantDeadlineListener(SecondQuadrantEvent secondQuadrantEvent) {
        long nowTimestamp = System.currentTimeMillis();
        Long coreId = secondQuadrantEvent.getCoreId();
        Long secondQuadrantId = secondQuadrantEvent.getId();
        Integer secondQuadrantCycle = secondQuadrantEvent.getCycle();
        // 判断消息是否有效需要执行（判断数据库的 deadline 与此消息的 deadline 是否一致）
        // （若不一致，则代表此消息已无效，无需重发，因为认定数据库里的 deadline 也有自己的消息，若没有，则代表消息丢失，再此弥补也无济于事）
        Date secondQuadrantDeadline = secondQuadrantEvent.getDeadline();
        long deadTimestamp = secondQuadrantDeadline.getTime();
        redisLock.tryLockDoSomething(CoreDeadlineConstants.SECOND_QUADRANT_DEADLINE_LOCK + secondQuadrantId, () -> {
            OkrCoreVO okrCoreVO = okrCoreService.searchOkrCore(coreId);
            Optional.ofNullable(okrCoreVO.getSecondQuadrantVO()).map(SecondQuadrantVO::getDeadline).map(Date::getTime).ifPresent(dbDeadline -> {
                if(!dbDeadline.equals(deadTimestamp)) {
                    return;
                }
                log.info("处理事件：内核 ID {}，第二象限 ID {}，第二象限截止时间 {}，第二象限周期 {}",
                        coreId, secondQuadrantId, secondQuadrantDeadline, secondQuadrantCycle);
                // 判断是否结束
                Boolean isOver = okrCoreVO.getIsOver();
                if(Boolean.TRUE.equals(isOver)) {
                    log.warn("OKR {} 已结束，第二象限 {} 停止对截止时间的刷新", coreId, secondQuadrantId);
                    return;
                }
                // 1. 获取一个正确的截止点
                long nextDeadTimestamp = DeadlineUtil.getNextDeadline(deadTimestamp, nowTimestamp, secondQuadrantCycle, TimeUnit.SECONDS);
                Date nextDeadline = new Date(nextDeadTimestamp);
                // 2. 更新截止时间
                if(nextDeadTimestamp != deadTimestamp) {
                    secondQuadrantService.updateDeadline(secondQuadrantId, nextDeadline);
                    // 更新的时候再发送
                    okrCoreService.noticeOkr(
                            getUserByCoreId(coreId),
                            okrCoreVO,
                            nextDeadline,
                            EmailTemplate.SHORT_TERM_NOTICE
                    );
                }
                // 3. 发起延时任务
                secondQuadrantEvent.setDeadline(nextDeadline);
                QuadrantDeadlineMessageUtil.scheduledUpdateSecondQuadrant(secondQuadrantEvent);
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
        Long coreId = thirdQuadrantEvent.getCoreId();
        Long thirdQuadrantId = thirdQuadrantEvent.getId();
        Integer thirdQuadrantCycle = thirdQuadrantEvent.getCycle();
        // 判断消息是否有效需要执行（判断数据库的 deadline 与此消息的 deadline 是否一致）
        // （若不一致，则代表此消息已无效，无需重发，因为认定数据库里的 deadline 也有自己的消息，若没有，则代表消息丢失，再此弥补也无济于事）
        Date thirdQuadrantDeadline = thirdQuadrantEvent.getDeadline();
        long deadTimestamp = thirdQuadrantDeadline.getTime();
        redisLock.tryLockDoSomething(CoreDeadlineConstants.THIRD_QUADRANT_DEADLINE_LOCK + thirdQuadrantId, () -> {
            OkrCoreVO okrCoreVO = okrCoreService.searchOkrCore(coreId);
            Optional.ofNullable(okrCoreVO.getThirdQuadrantVO()).map(ThirdQuadrantVO::getDeadline).map(Date::getTime).ifPresent(dbDeadline -> {
                if(!dbDeadline.equals(deadTimestamp)) {
                    return;
                }
                log.info("处理事件：内核 ID {}，第三象限 ID {}，第三象限截止时间 {}，第三象限周期 {}",
                        coreId, thirdQuadrantId, thirdQuadrantDeadline, thirdQuadrantCycle);
                // 判断是否结束
                Boolean isOver = okrCoreVO.getIsOver();
                if(Boolean.TRUE.equals(isOver)) {
                    log.warn("OKR {} 已结束，第三象限 {} 停止对截止时间的刷新", coreId, thirdQuadrantId);
                    return;
                }
                // 1. 获取一个正确的截止点
                long nextDeadTimestamp = DeadlineUtil.getNextDeadline(deadTimestamp, nowTimestamp, thirdQuadrantCycle, TimeUnit.SECONDS);
                Date nextDeadline = new Date(nextDeadTimestamp);
                // 2. 更新截止时间
                if(nextDeadTimestamp != deadTimestamp) {
                    thirdQuadrantService.updateDeadline(thirdQuadrantId, nextDeadline);
                    // 更新的时候再发送
                    okrCoreService.noticeOkr(
                            getUserByCoreId(coreId),
                            okrCoreVO,
                            nextDeadline,
                            EmailTemplate.LONG_TERM_NOTICE
                    );
                }
                // 3. 发起延时任务
                thirdQuadrantEvent.setDeadline(nextDeadline);
                QuadrantDeadlineMessageUtil.scheduledUpdateThirdQuadrant(thirdQuadrantEvent);
            });

        }, () -> {});
    }

}
