package cn.lbcmmszdntnt.domain.core.model.entity.inner;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName action
 */
@TableName(value ="action")
@Schema(description = "具体行动")
@Data
public class Action extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "第三象限 ID")
    private Long thirdQuadrantId;

    @Schema(description = "行动内容")
    private String content;

    @Schema(description = "是否完成")
    private Boolean isCompleted;

    private static final long serialVersionUID = 1L;
}