package cn.lbcmmszdntnt.domain.user.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-12-24
 * Time: 10:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WxCode2SessionDTO {

    @JsonProperty("appid")
    private String appid;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("js_code")
    private String jsCode;

    @JsonProperty("grant_type")
    private String grant_type;


}
