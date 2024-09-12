package cn.lbcmmszdntnt.domain.core.model.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName priority_number_one
 */
@TableName(value ="priority_number_one")
@Schema(description = "Priority 1")
@Data
public class PriorityNumberOne implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "第二象限 ID")
    private Long secondQuadrantId;

    @Schema(description = "P1 内容")
    private String content;

    @Schema(description = "是否完成")
    private Boolean isCompleted;

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