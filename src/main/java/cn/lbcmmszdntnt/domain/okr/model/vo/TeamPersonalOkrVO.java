package cn.lbcmmszdntnt.domain.okr.model.vo;


import cn.lbcmmszdntnt.domain.user.model.vo.UserVO;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 22:57
 */
@Schema(description = "团队个人 OKR 部分数据")
@Data
public class TeamPersonalOkrVO {

    @TableField("id")
    @Schema(description = "团队个人 OKR ID")
    private Long id;

    @TableField("core_id")
    @Schema(description = "内核 ID")
    private Long coreId;

    @TableField("team_id")
    @Schema(description = "团队 OKR ID")
    private Long teamId;

    @TableField("team_name")
    @Schema(description = "团队名")
    private String teamName;

    @TableField("is_over")
    @Schema(description = "是否结束")
    private Boolean isOver;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField("update_time")
    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema
    private UserVO manager;

}

