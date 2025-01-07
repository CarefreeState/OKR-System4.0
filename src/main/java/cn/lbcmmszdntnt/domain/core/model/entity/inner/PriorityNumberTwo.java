package cn.lbcmmszdntnt.domain.core.model.entity.inner;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName priority_number_two
 */
@TableName(value ="priority_number_two")
@Schema(description = "Priority 2")
@Data
public class PriorityNumberTwo extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "第二象限 ID")
    private Long secondQuadrantId;

    @Schema(description = "P2 内容")
    private String content;

    @Schema(description = "是否完成")
    private Boolean isCompleted;

    private static final long serialVersionUID = 1L;
}