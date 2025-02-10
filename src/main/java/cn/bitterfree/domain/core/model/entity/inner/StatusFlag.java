package cn.bitterfree.domain.core.model.entity.inner;

import cn.bitterfree.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName status_flag
 */
@TableName(value ="status_flag")
@Schema(description = "状态指标")
@Data
public class StatusFlag extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "第四象限 ID")
    private Long fourthQuadrantId;

    @Schema(description = "指标内容")
    private String label;

    @Schema(description = "颜色（#十六进制）")
    private String color;

    private static final long serialVersionUID = 1L;
}