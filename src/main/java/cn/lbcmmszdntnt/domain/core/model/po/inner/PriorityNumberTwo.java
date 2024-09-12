package cn.lbcmmszdntnt.domain.core.model.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName priority_number_two
 */
@TableName(value ="priority_number_two")
@Schema(description = "Priority 2")
@Data
public class PriorityNumberTwo implements Serializable {

    @Schema(name = "ID")
    private Long id;

    @Schema(name = "第二象限 ID")
    private Long secondQuadrantId;

    @Schema(name = "P2 内容")
    private String content;

    @Schema(name = "是否完成")
    private Boolean isCompleted;

    @Schema(name = "乐观锁")
    @JsonIgnore
    private Integer version;

    @Schema(name = "是否删除")
    @JsonIgnore
    private Boolean isDeleted;

    @Schema(name = "创建时间")
    private Date createTime;

    @Schema(name = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}