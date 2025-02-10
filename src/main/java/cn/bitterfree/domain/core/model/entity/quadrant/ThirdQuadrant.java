package cn.bitterfree.domain.core.model.entity.quadrant;

import cn.bitterfree.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName third_quadrant
 */
@TableName(value ="third_quadrant")
@Schema(description = "第三象限")
@Data
public class ThirdQuadrant extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "内核 ID")
    private Long coreId;

    @Schema(description = "截止时间")
    private Date deadline;

    private static final long serialVersionUID = 1L;
}