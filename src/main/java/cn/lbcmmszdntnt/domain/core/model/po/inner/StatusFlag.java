package cn.lbcmmszdntnt.domain.core.model.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName status_flag
 */
@TableName(value ="status_flag")
@Schema(description = "状态指标")
@Data
public class StatusFlag implements Serializable {

    @Schema(name = "ID")
    private Long id;

    @Schema(name = "第四象限 ID")
    private Long fourthQuadrantId;

    @Schema(name = "指标内容")
    private String label;

    @Schema(name = "颜色（#十六进制）")
    private String color;

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