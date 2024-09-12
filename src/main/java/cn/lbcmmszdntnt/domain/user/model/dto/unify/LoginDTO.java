package cn.lbcmmszdntnt.domain.user.model.dto.unify;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.user.model.dto.EmailLoginDTO;
import cn.lbcmmszdntnt.domain.user.model.dto.WxLoginDTO;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import cn.lbcmmszdntnt.util.convert.ObjectUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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

    @Schema(nullable = true)
    @Valid
    private EmailLoginDTO emailLoginDTO;

    @Schema(nullable = true)
    @Valid
    private WxLoginDTO wxLoginDTO;

    public void validate() {
        ObjectUtil.stream(this, Object.class)
                .map(Objects::nonNull)
                .findAny()
                .orElseThrow(() ->
                        new GlobalServiceException("没有携带登录数据", GlobalServiceStatusCode.PARAM_IS_BLANK));
    }

}
