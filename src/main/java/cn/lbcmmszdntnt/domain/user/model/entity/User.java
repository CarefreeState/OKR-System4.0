package cn.lbcmmszdntnt.domain.user.model.entity;

import cn.lbcmmszdntnt.common.base.BaseIncrIDEntity;
import cn.lbcmmszdntnt.domain.user.enums.UserType;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @TableName user
 */
@TableName(value ="user")
@Schema(description = "用户")
@Data
public class User extends BaseIncrIDEntity implements Serializable {

    @Schema(description = "openid")
    private String openid;

    @Schema(description = "unionid")
    private String unionid;

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

    private static final long serialVersionUID = 1L;
}