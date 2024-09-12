package cn.lbcmmszdntnt.domain.core.model.po.quadrant.vo;

import cn.lbcmmszdntnt.domain.core.model.po.inner.PriorityNumberOne;
import cn.lbcmmszdntnt.domain.core.model.po.inner.PriorityNumberTwo;
import cn.lbcmmszdntnt.domain.core.model.po.quadrant.SecondQuadrant;
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
@Schema(description = "第二象限详细信息")
@Data
public class SecondQuadrantVO extends SecondQuadrant {

    @Schema
    private List<PriorityNumberOne> priorityNumberOnes;

    @Schema
    private List<PriorityNumberTwo> priorityNumberTwos;
}
