package cn.bitterfree.domain.user.model.entity;

import cn.bitterfree.common.base.BaseIncrIDEntity;
import cn.bitterfree.domain.user.enums.UserType;
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

    private String openid;

    private String unionid;

    private String username;

    private String nickname;

    private String password;

    private String photo;

    private String email;

    private String phone;

    private UserType userType;

    private static final long serialVersionUID = 1L;
}