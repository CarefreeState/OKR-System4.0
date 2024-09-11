package cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo;

import cn.lbcmmszdntnt.domain.core.model.po.inner.StatusFlag;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.FourthQuadrant;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:31
 */
@Schema(description = "第四象限详细信息")
@Data
public class FourthQuadrantVO extends FourthQuadrant {

    @SchemaProperty(name = "状态指标列表")
    private List<StatusFlag> statusFlags;

}
