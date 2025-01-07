package cn.lbcmmszdntnt.domain.core.model.entity.quadrant;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName fourth_quadrant
 */
@TableName(value ="fourth_quadrant")
@Schema(description = "第四象限")
@Data
public class FourthQuadrant extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "内核 ID")
    private Long coreId;

    private static final long serialVersionUID = 1L;
}