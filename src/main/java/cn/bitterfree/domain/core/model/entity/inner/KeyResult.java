package cn.bitterfree.domain.core.model.entity.inner;

import cn.bitterfree.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName key_result
 */
@TableName(value ="key_result")
@Schema(description = "关键结果")
@Data
public class KeyResult extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "第一象限 ID")
    private Long firstQuadrantId;

    @Schema(description = "关键结果内容")
    private String content;

    @Schema(description = "完成概率")
    private Integer probability;

    private static final long serialVersionUID = 1L;
}