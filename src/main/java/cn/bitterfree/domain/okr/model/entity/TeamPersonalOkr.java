package cn.bitterfree.domain.okr.model.entity;

import cn.bitterfree.common.base.BaseIncrIDEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName team_personal_okr
 */
@TableName(value ="team_personal_okr")
@Schema(description = "团队个人 OKR")
@Data
public class TeamPersonalOkr extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "内核 ID")
    private Long coreId;

    @Schema(description = "团队 ID")
    private Long teamId;

    @Schema(description = "成员 ID")
    private Long userId;

    private static final long serialVersionUID = 1L;
}