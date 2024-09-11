package cn.lbcmmszdntnt.domain.medal.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
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

    @SchemaProperty(name = "勋章 ID")
    private Long medalId;

    @SchemaProperty(name = "勋章名称")
    private String name;

    @SchemaProperty(name = "勋章描述")
    private String description;

    @SchemaProperty(name = "勋章 URL")
    private String url;

    @SchemaProperty(name = "勋章等级")
    private Integer level;

    @SchemaProperty(name = "勋章是否已读")
    private Boolean isRead;

    @SchemaProperty(name = "勋章颁布时间")
    private Date issueTime;
}
