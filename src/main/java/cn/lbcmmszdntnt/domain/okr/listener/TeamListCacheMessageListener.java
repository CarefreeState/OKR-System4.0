package cn.lbcmmszdntnt.domain.okr.listener;

import cn.lbcmmszdntnt.domain.okr.constants.OkrConstants;
import cn.lbcmmszdntnt.domain.okr.util.TeamOkrUtil;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-17
 * Time: 17:30
 */
@Component
public class TeamListCacheMessageListener {

    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(name = OkrConstants.TEAM_OKR_CLEAR_CACHE_DELAY_DIRECT, delayed = "true"),
            value = @Queue(name = OkrConstants.TEAM_OKR_CLEAR_CACHE_QUEUE),
            key = OkrConstants.TEAM_OKR_CLEAR_CACHE
    ))
    public void clearTeamListCacheListener(Long teamId) {
        TeamOkrUtil.deleteChildListCache(teamId);
    }

}
