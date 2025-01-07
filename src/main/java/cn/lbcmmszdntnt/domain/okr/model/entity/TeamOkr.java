package cn.lbcmmszdntnt.domain.okr.model.entity;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName team_okr
 */
@TableName(value ="team_okr")
@Schema(description = "团队 OKR")
@Data
public class TeamOkr extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "内核 ID")
    private Long coreId;

    @Schema(description = "父团队 ID")
    private Long parentTeamId;

    @Schema(description = "管理者 ID")
    private Long managerId;

    @Schema(description = "团队名")
    private String teamName;

    private static final long serialVersionUID = 1L;
}