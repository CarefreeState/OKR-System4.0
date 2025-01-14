package cn.lbcmmszdntnt.wxtoken.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.awt.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-14
 * Time: 3:05
 */
@Data
public class LineColor {

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
