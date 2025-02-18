package cn.bitterfree.api.mq.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-18
 * Time: 15:45
 */
@Data
public class DelayExchangeVO {

    @JsonProperty("messages_delayed")
    public Integer messagesDelayed; // 暂时我只需要这个

}
