package cn.lbcmmszdntnt.wxtoken.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 3:58
 */
@Data
public class JsCode2SessionVO {

    @JsonProperty("openid")
    private String openid;

    @JsonProperty("unionid")
    private String unionid;

}
