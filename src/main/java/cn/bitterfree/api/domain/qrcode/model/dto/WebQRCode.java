package cn.bitterfree.api.domain.qrcode.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.awt.*;

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

    @JsonProperty("line_color")
    private LineColor lineColor;

    @Data
    public static class LineColor {

        @JsonProperty("r")
        private Integer red;

        @JsonProperty("g")
        private Integer green;

        @JsonProperty("b")
        private Integer blue;

        public Color color() {
            return new Color(red, green, blue);
        }

    }

}
