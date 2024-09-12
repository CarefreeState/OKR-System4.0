package cn.lbcmmszdntnt.domain.okr.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "内核 ID")
    private Long coreId;

    @Schema(description = "父团队 ID")
    private Long parentTeamId;

    @Schema(description = "管理者 ID")
    private Long managerId;

    @Schema(description = "团队名")
    private String teamName;

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