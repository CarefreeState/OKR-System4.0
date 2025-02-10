package cn.bitterfree.domain.core.model.entity.inner;

import cn.bitterfree.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName priority_number_one
 */
@TableName(value ="priority_number_one")
@Schema(description = "Priority 1")
@Data
public class PriorityNumberOne extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "第二象限 ID")
    private Long secondQuadrantId;

    @Schema(description = "P1 内容")
    private String content;

    @Schema(description = "是否完成")
    private Boolean isCompleted;

    private static final long serialVersionUID = 1L;
}