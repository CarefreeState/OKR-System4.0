package cn.lbcmmszdntnt.domain.okr.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName team_okr
 */
@TableName(value ="team_okr")
@Schema(description = "团队 OKR")
@Data
public class TeamOkr implements Serializable {

    @SchemaProperty(name = "ID")
    private Long id;

    @SchemaProperty(name = "内核 ID")
    private Long coreId;

    @SchemaProperty(name = "父团队 ID")
    private Long parentTeamId;

    @SchemaProperty(name = "管理者 ID")
    private Long managerId;

    @SchemaProperty(name = "团队名")
    private String teamName;

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