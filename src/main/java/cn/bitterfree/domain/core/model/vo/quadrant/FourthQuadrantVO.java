package cn.bitterfree.domain.core.model.vo.quadrant;

import cn.bitterfree.domain.core.model.entity.inner.StatusFlag;
import cn.bitterfree.domain.core.model.entity.quadrant.FourthQuadrant;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema
    private List<StatusFlag> statusFlags;

}
