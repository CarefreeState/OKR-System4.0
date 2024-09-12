package cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo;

import cn.lbcmmszdntnt.domain.core.model.po.inner.Action;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.ThirdQuadrant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:32
 */
@Schema(description = "第三象限详细信息")
@Data
public class ThirdQuadrantVO extends ThirdQuadrant {

    @Schema
    private List<Action> actions;

}
