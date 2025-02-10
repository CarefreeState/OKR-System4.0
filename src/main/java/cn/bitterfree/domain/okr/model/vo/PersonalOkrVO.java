package cn.bitterfree.domain.okr.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 21:39
 */
@Schema(description = "个人 OKR 部分数据")
@Data
public class PersonalOkrVO {

    @TableField("id")
    @Schema(description = "个人 OKR ID")
    private Long id;

    @TableField("core_id")
    @Schema(description = "内核 ID")
    private Long coreId;

    @TableField("objective")
    @Schema(description = "目标")
    private String objective;

    @TableField("is_over")
    @Schema(description = "是否结束")
    private Boolean isOver;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField("update_time")
    @Schema(description = "更新时间")
    private Date updateTime;
}
