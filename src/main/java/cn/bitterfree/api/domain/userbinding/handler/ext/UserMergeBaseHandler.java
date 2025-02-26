package cn.bitterfree.api.domain.userbinding.handler.ext;

import cn.bitterfree.api.domain.user.service.UserService;
import cn.bitterfree.api.domain.userbinding.handler.UserMergeHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-26
 * Time: 0:34
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserMergeBaseHandler extends UserMergeHandler {

    private final UserService userService;

    @Override
    @Transactional
    public List<String> handle(Long mainUserId, Long userId) {
        log.info("合并基本信息数据 {} <- {}", mainUserId, userId);
        List<String> redisKeys = new ArrayList<>(userService.mergeUser(mainUserId, userId));
        redisKeys.addAll(super.doNextHandler(mainUserId, userId));
        return redisKeys;
    }
}
