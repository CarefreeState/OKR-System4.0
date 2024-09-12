package cn.lbcmmszdntnt.domain.user.model.dto;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.email.util.EmailValidator;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 14:26
 */
@Schema(description = "邮箱验证数据")
@Data
public class EmailCheckDTO {

    @SchemaProperty(name = "验证类型")
    @NotBlank(message = "验证类型不能为空")
    private String type;

    @SchemaProperty(name = "email")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不合法")
    private String email;

}
