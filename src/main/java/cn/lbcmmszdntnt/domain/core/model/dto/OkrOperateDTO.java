package cn.lbcmmszdntnt.domain.core.model.dto;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 19:18
 */
@Schema(description = "OKR 操作数据")
@Data
public class OkrOperateDTO {

    @SchemaProperty(name = "场景")
    private String scene;

    @SchemaProperty(name = "团队 OKR ID")
    private Long teamOkrId;

    @SchemaProperty(name = "邀请密钥")
    private String secret;

    @SchemaProperty(name = "邀请码类型")
    private String type;

    @SchemaProperty(name = "团队名")
    private String teamName;


    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(scene)) {
            messageBuilder.append("\n-> 缺少场景值");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
