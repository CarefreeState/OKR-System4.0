package cn.bitterfree.api.domain.userbinding.handler.chain;

import cn.bitterfree.api.domain.userbinding.constants.UserBindingConstants;
import cn.bitterfree.api.domain.userbinding.handler.UserMergeHandler;
import cn.bitterfree.api.domain.userbinding.handler.ext.UserMergeBaseHandler;
import cn.bitterfree.api.interceptor.constants.UserIdConstants;
import cn.bitterfree.api.jwt.config.JwtProperties;
import cn.bitterfree.api.redis.cache.RedisCache;
import cn.bitterfree.api.redis.lock.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-26
 * Time: 0:21
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserMergeHandlerChain extends UserMergeHandler implements InitializingBean {

    private final RedisLock redisLock;

    private final UserMergeBaseHandler userMergeBaseHandler;

    private final RedisCache redisCache;

    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public List<String> handle(Long mainUserId, Long userId) {
        if(mainUserId.equals(userId)) {
            return Collections.emptyList();
        }
//        // 对用户 id 进行加锁（按照大小顺序加锁的方式避免死锁）
//        redisLock.tryLockDoSomething(UserBindingConstants.USER_BINDING_ID_LOCK + Math.min(mainUserId, userId), () -> {
//            redisLock.tryLockDoSomething(UserBindingConstants.USER_BINDING_ID_LOCK + Math.max(mainUserId, userId), () -> {
//                log.info("开始合并用户多账号 {} <- {}", mainUserId, userId);
//                List<String> redisKeys = super.doNextHandler(mainUserId, userId);
//                log.info("用户多账号 {} <- {} 合并完毕，需删除缓存 {}！", mainUserId, userId, redisKeys);
//                redisCache.deleteObjects(redisKeys);
//                // 缓存 userId -> mainUserId 的映射
//                log.info("设置 {} -> {} 的映射", userId, mainUserId);
//                redisCache.setObject(UserIdConstants.USER_ID_REDIRECT + userId, mainUserId,
//                        jwtProperties.getTtl(), jwtProperties.getUnit());
//            }, () -> {});
//        }, () -> {});
        // 对用户 id 进行加锁（通过 timeout 的方式避免死锁）
        redisLock.tryLockDoSomething(UserBindingConstants.USER_BINDING_ID_LOCK + mainUserId, () -> {
            redisLock.tryLockDoSomething(UserBindingConstants.USER_BINDING_ID_LOCK + userId, () -> {
                log.info("开始合并用户多账号 {} <- {}", mainUserId, userId);
                List<String> redisKeys = super.doNextHandler(mainUserId, userId);
                log.info("用户多账号 {} <- {} 合并完毕，需删除缓存 {}！", mainUserId, userId, redisKeys);
                redisCache.deleteObjects(redisKeys);
                // 缓存 userId -> mainUserId 的映射
                log.info("设置 {} -> {} 的映射", userId, mainUserId);
                redisCache.setObject(UserIdConstants.USER_ID_REDIRECT + userId, mainUserId,
                        jwtProperties.getTtl(), jwtProperties.getUnit());
            }, () -> {});
        }, () -> {});
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        UserMergeHandler.addHandlerAfter(userMergeBaseHandler, this);
    }
}
