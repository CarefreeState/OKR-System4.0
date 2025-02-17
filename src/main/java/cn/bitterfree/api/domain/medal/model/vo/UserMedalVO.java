package cn.bitterfree.api.domain.medal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-08
 * Time: 0:40
 */
@Schema(description = "用户勋章")
@Data
public class UserMedalVO {

    @Schema(description = "勋章 ID")
    private Long medalId;

    @Schema(description = "勋章名称")
    private String name;

    @Schema(description = "勋章描述")
    private String description;

    @Schema(description = "勋章 URL")
    private String url;

    @Schema(description = "勋章等级")
    private Integer level;

    @Schema(description = "勋章是否已读")
    private Boolean isRead;

    @Schema(description = "勋章颁布时间")
    private Date issueTime;
}
