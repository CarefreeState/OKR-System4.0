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
 * Time: 4:01
 */
@Data
@Builder
public class JsCode2SessionDTO {

    @JsonProperty("js_code")
    private String jsCode;

    @JsonProperty("grant_type")
    private String grantType;

    public JsCode2SessionDTO(String jsCode, String grantType) {
        this.jsCode = jsCode;
        this.grantType = StringUtils.hasText(grantType) ? grantType : "authorization_code";
    }

}
