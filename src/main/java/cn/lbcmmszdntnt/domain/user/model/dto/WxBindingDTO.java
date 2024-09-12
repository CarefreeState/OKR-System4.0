package cn.lbcmmszdntnt.domain.user.model.dto;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Schema(description = "userId")
    @NotNull(message = "userId 不能为空")
    private Long userId;

    @Schema(description = "code")
    @NotBlank(message = "code 不能为空")
    private String code;

    @Schema(description = "随机码")
    @NotBlank(message = "随机码不能为空")
    private String randomCode;

}
