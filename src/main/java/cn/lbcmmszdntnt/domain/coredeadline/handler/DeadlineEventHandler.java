package cn.lbcmmszdntnt.domain.coredeadline.handler;


import cn.lbcmmszdntnt.domain.coredeadline.handler.event.DeadlineEvent;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-12
 * Time: 9:44
 */
public abstract class DeadlineEventHandler {

    private DeadlineEventHandler deadlineEventHandler;

    public abstract void handle(DeadlineEvent deadlineEvent, long nowTimestamp, Boolean needSend);

    public void setNextHandler(DeadlineEventHandler deadlineEventHandler) {
        this.deadlineEventHandler = deadlineEventHandler;
    }

    protected void doNextHandler(DeadlineEvent deadlineEvent, long nowTimestamp, Boolean needSend) {
        if(Objects.nonNull(deadlineEventHandler)) {
            deadlineEventHandler.handle(deadlineEvent, nowTimestamp, needSend);
        }
    }
}
