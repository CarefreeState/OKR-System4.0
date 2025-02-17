package cn.bitterfree.api.domain.user.model.vo;

import cn.bitterfree.api.domain.user.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-07
 * Time: 23:16
 */
@Schema(description = "用户信息")
@Data
public class UserVO {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "openid")
    private String openid;

    @Schema(description = "unionid")
    private String unionid;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String photo;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户类型")
    private UserType userType;

}