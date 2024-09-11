package cn.lbcmmszdntnt.domain.user.model.dto;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.email.util.EmailValidator;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
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
    private String type;

    @SchemaProperty(name = "email")
    private String email;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(type)) {
            messageBuilder.append("\n-> 验证类型 为 空");
        }
        if(!StringUtils.hasText(email) || !EmailValidator.isEmailAccessible(email)) {
            messageBuilder.append("\n-> email 非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }
}
