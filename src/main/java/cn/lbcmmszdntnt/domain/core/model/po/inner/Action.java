package cn.lbcmmszdntnt.domain.core.model.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName action
 */
@TableName(value ="action")
@Schema(description = "具体行动")
@Data
public class Action implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "第三象限 ID")
    private Long thirdQuadrantId;

    @Schema(description = "行动内容")
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