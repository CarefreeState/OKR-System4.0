package cn.lbcmmszdntnt.domain.okr.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-03
 * Time: 16:50
 */
@Schema(description = "团队成员数据")
@Data
public class TeamMemberVO {

    @TableField("id")
    @Schema(description = "团队个人 OKR ID")
    private Long id;

    @TableField("user_id")
    @Schema(description = "用户 ID")
    private Long userId;

    @TableField("nickname")
    @Schema(description = "昵称")
    private String nickname;

    @TableField("photo")
    @Schema(description = "头像")
    private String photo;

    @TableField("email")
    @Schema(description = "邮箱")
    private String email;

    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    @TableField("create_time")
    @Schema(description = "加入时间")
    private Date createTime;

    @Schema(description = "是否有子团队")
    private Boolean isExtend;

}
