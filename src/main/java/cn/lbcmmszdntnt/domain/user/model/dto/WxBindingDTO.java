package cn.lbcmmszdntnt.domain.user.model.dto;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 15:57
 */
@Schema(description = "微信验证数据")
@Data
public class WxBindingDTO {

    @SchemaProperty(name = "userId")
    private Long userId;

    @SchemaProperty(name = "code")
    private String code;

    @SchemaProperty(name = "随机码")
    private String randomCode;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(userId)) {
            messageBuilder.append("\n-> userId 为空");
        }
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> code 为空");
        }
        if(!StringUtils.hasText(randomCode)) {
            messageBuilder.append("\n-> randomCode 为空");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }
}
