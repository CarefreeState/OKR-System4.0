package cn.lbcmmszdntnt.domain.okr.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName team_personal_okr
 */
@TableName(value ="team_personal_okr")
@Schema(description = "团队个人 OKR")
@Data
public class TeamPersonalOkr implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "内核 ID")
    private Long coreId;

    @Schema(description = "团队 ID")
    private Long teamId;

    @Schema(description = "成员 ID")
    private Long userId;

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