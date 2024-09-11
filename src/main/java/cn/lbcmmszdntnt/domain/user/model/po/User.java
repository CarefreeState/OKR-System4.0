package cn.lbcmmszdntnt.domain.user.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user
 */
@TableName(value ="user")
@Schema(description = "用户")
@Data
public class User implements Serializable {

    @SchemaProperty(name = "ID")
    private Long id;

    @SchemaProperty(name = "openid")
    private String openid;

    @SchemaProperty(name = "unionid")
    private String unionid;

    @SchemaProperty(name = "昵称")
    private String nickname;

    @SchemaProperty(name = "头像")
    private String photo;

    @SchemaProperty(name = "邮箱")
    private String email;

    @SchemaProperty(name = "手机号")
    private String phone;

    @SchemaProperty(name = "乐观锁")
    @JsonIgnore
    private Integer version;

    @SchemaProperty(name = "是否删除")
    @JsonIgnore
    private Boolean isDeleted;

    @SchemaProperty(name = "创建时间")
    private Date createTime;

    @SchemaProperty(name = "更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}