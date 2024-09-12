package cn.lbcmmszdntnt.domain.okr.model.dto;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 1:43
 */
@Schema(description = "授权成员所需数据")
@Data
public class GrantDTO {

    @Schema(description = "团队 OKR ID")
    private Long teamId;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "团队名")
    private String teamName;

}
