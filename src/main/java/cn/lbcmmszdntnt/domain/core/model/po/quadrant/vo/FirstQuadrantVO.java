package cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo;

import cn.lbcmmszdntnt.domain.core.model.po.inner.KeyResult;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.FirstQuadrant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 23:09
 */
@Schema(description = "第一象限详情信息")
@Data
public class FirstQuadrantVO extends FirstQuadrant {

    @Schema
    private List<KeyResult> keyResults;

}
