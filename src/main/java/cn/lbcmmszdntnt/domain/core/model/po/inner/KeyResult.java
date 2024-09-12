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

    @Schema(name = "ID")
    private Long id;

    @Schema(name = "第一象限 ID")
    private Long firstQuadrantId;

    @Schema(name = "关键结果内容")
    private String content;

    @Schema(name = "完成概率")
    private Integer probability;

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