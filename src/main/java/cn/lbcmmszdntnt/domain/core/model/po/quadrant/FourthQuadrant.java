package cn.lbcmmszdntnt.domain.core.model.po.quadrant;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName fourth_quadrant
 */
@TableName(value ="fourth_quadrant")
@Schema(description = "第四象限")
@Data
public class FourthQuadrant implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "内核 ID")
    private Long coreId;

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