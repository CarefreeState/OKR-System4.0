package cn.bitterfree.api.domain.userbinding.handler.chain;

import cn.bitterfree.api.domain.userbinding.handler.UserMergeHandler;
import cn.bitterfree.api.domain.userbinding.handler.ext.UserMergeBaseHandler;
import cn.bitterfree.api.redis.cache.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    private final UserMergeBaseHandler userMergeBaseHandler;

    private final RedisCache redisCache;

    @Override
    @Transactional
    public List<String> handle(Long mainUserId, Long userId) {
        log.warn("开始合并用户多账号 {} <- {}", mainUserId, userId);
        List<String> redisKeys = super.doNextHandler(mainUserId, userId);
        log.warn("用户多账号 {} <- {} 合并完毕，需删除缓存 {}！", mainUserId, userId, redisKeys);
        redisCache.deleteObjects(redisKeys);
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        UserMergeHandler.addHandlerAfter(userMergeBaseHandler, this);
    }
}
