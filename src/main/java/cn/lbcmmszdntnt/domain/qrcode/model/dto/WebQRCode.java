package cn.lbcmmszdntnt.domain.qrcode.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 2:49
 */
@Data
public class WebQRCode {

    @JsonProperty("page")
    private String page;

    @JsonProperty("width")
    private Integer width;

}
