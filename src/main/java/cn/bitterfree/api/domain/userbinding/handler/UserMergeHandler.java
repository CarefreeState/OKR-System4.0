package cn.bitterfree.api.domain.userbinding.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-26
 * Time: 0:17
 */
public abstract class UserMergeHandler {

    private UserMergeHandler next;

    public abstract List<String> handle(Long mainUserId, Long userId);

    public void setNextHandler(UserMergeHandler nextHandler) {
        this.next = nextHandler;
    }

    protected List<String> doNextHandler(Long mainUserId, Long userId) {
        if(Objects.nonNull(this.next)) {
            return next.handle(mainUserId, userId);
        } else {
            return new ArrayList<>();
        }
    }

    // 添加处理器在目标处理器之后
    public static void addHandlerAfter(UserMergeHandler afterHandler, UserMergeHandler targetHandler) {
        Optional.ofNullable(targetHandler.next).ifPresent(afterHandler::setNextHandler);
        targetHandler.setNextHandler(afterHandler);
    }

}
