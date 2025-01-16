package cn.lbcmmszdntnt.wxtoken.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 2:51
 */
@Data
public class WxQRCode {

    @JsonProperty("scene")
    private String scene;

    @JsonProperty("check_path")
    private Boolean checkPath;

    @JsonProperty("env_version")
    private String envVersion;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("auto_color")
    private Boolean autoColor;

    @JsonProperty("line_color")
    private LineColor lineColor;

    @JsonProperty("is_hyaline")
    private Boolean isHyaline;

    @Data
    public static class LineColor {

        @JsonProperty("r")
        private Integer red;

        @JsonProperty("g")
        private Integer green;

        @JsonProperty("b")
        private Integer blue;

    }

}
