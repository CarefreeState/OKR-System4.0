package cn.lbcmmszdntnt.domain.user.model.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 11:54
 */
@Schema(description = "邮箱登录数据")
@Data
public class EmailLoginDTO {

    @Schema(description = "code")
    @NotBlank(message = "code 不能为空")
    private String code;

    @Schema(description = "email")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不合法")
    private String email;

    public User transToUser() {
        User user = new User();
        user.setEmail(this.email);
        return user;
    }

    public static EmailLoginDTO create(Map<?, ?> data) {
        return BeanUtil.mapToBean(data, EmailLoginDTO.class, Boolean.FALSE, new CopyOptions());
    }

}
