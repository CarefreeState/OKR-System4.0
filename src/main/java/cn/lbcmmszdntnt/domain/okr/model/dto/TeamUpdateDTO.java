package cn.lbcmmszdntnt.domain.okr.model.dto;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-14
 * Time: 23:05
 */
@Schema(description = "Team OKR 更新数据")
@Data
public class TeamUpdateDTO {

    @Schema(description = "团队 OKR ID")
    private Long id;

    @Schema(description = "团队名")
    private String teamName;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("\n-> 团队 OKR ID 为 null");
        }
        if(!StringUtils.hasText(teamName)) {
            messageBuilder.append("\n-> teamName没有内容");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}