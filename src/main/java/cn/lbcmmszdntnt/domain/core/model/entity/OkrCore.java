package cn.lbcmmszdntnt.domain.core.model.entity;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName okr_core
 */
@TableName(value ="okr_core")
@Schema(description = "OKR 内核")
@Data
public class OkrCore extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "庆祝日（星期）")
    private Integer celebrateDay;

    @Schema(description = "第二象限周期（秒）")
    private Integer secondQuadrantCycle;

    @Schema(description = "第三象限周期（秒）")
    private Integer thirdQuadrantCycle;

    @Schema(description = "是否结束")
    private Boolean isOver;

    @Schema(description = "总结")
    private String summary;

    @Schema(description = "完成度")
    private Integer degree;

    private static final long serialVersionUID = 1L;
}