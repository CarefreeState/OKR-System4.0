package cn.bitterfree.wxtoken.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 3:26
 */
@Data
@Builder
public class AccessTokenDTO {

    @JsonProperty("grant_type")
    private String grantType;

    @JsonProperty("appid")
    private String appid;

    @JsonProperty("secret")
    private String secret;

    public AccessTokenDTO(String grantType, String appid, String secret) {
        this.grantType = StringUtils.hasText(grantType) ? grantType : "client_credential";
        this.appid = appid;
        this.secret = secret;
    }
}
