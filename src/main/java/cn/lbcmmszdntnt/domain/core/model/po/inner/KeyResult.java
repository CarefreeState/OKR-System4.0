package cn.lbcmmszdntnt.domain.core.model.po.inner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName key_result
 */
@TableName(value ="key_result")
@Schema(description = "关键结果")
@Data
public class KeyResult implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "第一象限 ID")
    private Long firstQuadrantId;

    @Schema(description = "关键结果内容")
    private String content;

    @Schema(description = "完成概率")
    private Integer probability;

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