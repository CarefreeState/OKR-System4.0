package cn.lbcmmszdntnt.domain.core.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName okr_core
 */
@TableName(value ="okr_core")
@Schema(description = "OKR 内核")
@Data
public class OkrCore implements Serializable {

    @Schema(description = "ID")
    private Long id;

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

    @Schema(description = "乐观锁")
    @JsonIgnore
    private Integer version;

    @Schema(description = "是否删除")
    @JsonIgnore
    private Boolean isDeleted;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}