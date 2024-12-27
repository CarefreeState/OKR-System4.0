package cn.lbcmmszdntnt.domain.user.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-12-24
 * Time: 9:51
 */
@Data
public class WxCode2SessionVO {

    @JsonProperty("openid")
    private String openId;

    @JsonProperty("unionid")
    private String unionId;

    @JsonProperty("session_key")
    private String sessionKey;

}
