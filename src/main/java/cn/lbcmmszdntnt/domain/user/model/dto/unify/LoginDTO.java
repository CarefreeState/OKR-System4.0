package cn.lbcmmszdntnt.domain.user.model.dto.unify;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.model.dto.EmailLoginDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.WxLoginDTO;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-24
 * Time: 11:49
 */
@Schema(description = "登录数据")
@Data
public class LoginDTO {

    @SchemaProperty(name = "邮箱登录数据")
    private EmailLoginDTO emailLoginDTO;

    @SchemaProperty(name = "微信小程序登录数据")
    private WxLoginDTO wxLoginDTO;

    public void validate() {
        try {
            Field[] fields = LoginDTO.class.getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                Object o = field.get(this);
                field.setAccessible(false);
                if(Objects.nonNull(o)) {
                    return;
                }
            }
            throw new GlobalServiceException("没有携带登录数据", GlobalServiceStatusCode.PARAM_IS_BLANK);
        } catch (IllegalAccessException e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }

    public EmailLoginDTO createEmailLoginDTO() {
        return this.emailLoginDTO;
    }

    public WxLoginDTO createWxLoginDTO() {
        return this.wxLoginDTO;
    }

}
