package cn.lbcmmszdntnt.domain.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
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

}