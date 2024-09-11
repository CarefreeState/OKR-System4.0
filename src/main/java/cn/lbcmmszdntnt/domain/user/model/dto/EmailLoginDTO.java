package cn.lbcmmszdntnt.domain.user.model.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.email.util.EmailValidator;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

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

    @SchemaProperty(name = "code")
    private String code;

    @SchemaProperty(name = "email")
    private String email;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> code 为 空");
        }
        if(!StringUtils.hasText(email) || !EmailValidator.isEmailAccessible(email)) {
            messageBuilder.append("\n-> email 非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

    public User transToUser() {
        User user = new User();
        user.setEmail(this.email);
        return user;
    }

    public static EmailLoginDTO create(Map<?, ?> data) {
        return BeanUtil.mapToBean(data, EmailLoginDTO.class, Boolean.FALSE, new CopyOptions());
    }

}
