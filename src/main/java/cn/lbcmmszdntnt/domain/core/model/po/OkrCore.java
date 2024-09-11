package cn.lbcmmszdntnt.domain.core.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
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

    @SchemaProperty(name = "ID")
    private Long id;

    @SchemaProperty(name = "庆祝日（星期）")
    private Integer celebrateDay;

    @SchemaProperty(name = "第二象限周期（秒）")
    private Integer secondQuadrantCycle;

    @SchemaProperty(name = "第三象限周期（秒）")
    private Integer thirdQuadrantCycle;

    @SchemaProperty(name = "是否结束")
    private Boolean isOver;

    @SchemaProperty(name = "总结")
    private String summary;

    @SchemaProperty(name = "完成度")
    private Integer degree;

    @SchemaProperty(name = "乐观锁")
    @JsonIgnore
    private Integer version;

    @SchemaProperty(name = "是否删除")
    @JsonIgnore
    private Boolean isDeleted;

    @SchemaProperty(name = "创建时间")
    private Date createTime;

    @SchemaProperty(name = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}