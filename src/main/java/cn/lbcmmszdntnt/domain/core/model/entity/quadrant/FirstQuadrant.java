package cn.lbcmmszdntnt.domain.core.model.entity.quadrant;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName first_quadrant
 */
@TableName(value ="first_quadrant")
@Schema(description = "第一象限")
@Data
public class FirstQuadrant extends BaseIncrIDEntity implements Serializable {

    private Long coreId;

    @Schema(description = "目标")
    private String objective;

    @Schema(description = "截止时间")
    private Date deadline;

    private static final long serialVersionUID = 1L;
}