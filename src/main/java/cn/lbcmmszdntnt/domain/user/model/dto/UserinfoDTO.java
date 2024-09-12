package cn.lbcmmszdntnt.domain.user.model.dto;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-05
 * Time: 18:45
 */
@Schema(description = "用户完善信息")
@Data
public class UserinfoDTO {

    @Schema(description = "昵称")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

}
