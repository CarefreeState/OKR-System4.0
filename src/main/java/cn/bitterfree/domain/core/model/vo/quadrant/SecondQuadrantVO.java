package cn.bitterfree.domain.core.model.vo.quadrant;

import cn.bitterfree.domain.core.model.entity.inner.PriorityNumberOne;
import cn.bitterfree.domain.core.model.entity.inner.PriorityNumberTwo;
import cn.bitterfree.domain.core.model.entity.quadrant.SecondQuadrant;
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
