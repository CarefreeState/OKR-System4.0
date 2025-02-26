package cn.bitterfree.api.domain.okr.handler.ext;

import cn.bitterfree.api.domain.core.service.OkrOperateService;
import cn.bitterfree.api.domain.userbinding.handler.UserMergeHandler;
import cn.bitterfree.api.domain.userbinding.handler.ext.UserMergeBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-26
 * Time: 1:13
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OkrUserMergeHandler extends UserMergeHandler implements InitializingBean {

    private final List<OkrOperateService> okrOperateServiceList;

    private final UserMergeBaseHandler userMergeBaseHandler;

    @Override
    @Transactional
    public List<String> handle(Long mainUserId, Long userId) {
        log.info("合并 OKR 数据 {} <- {}", mainUserId, userId);
        List<String> redisKeys = okrOperateServiceList.stream()
                .flatMap(okrOperateService -> okrOperateService.mergeUserOkr(mainUserId, userId).stream())
                .collect(Collectors.toList());
        redisKeys.addAll(super.doNextHandler(mainUserId, userId));
        return redisKeys;
    }

    @Override
    public void afterPropertiesSet() {
        UserMergeHandler.addHandlerAfter(this, userMergeBaseHandler);
    }
}
