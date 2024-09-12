package cn.lbcmmszdntnt.domain.qrcode.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-21
 * Time: 9:40
 */
@Schema(description = "小程序登录码")
@Builder
@Data
public class LoginQRCodeVO {

    @Schema(description = "小程序码地址")
    private String path;

    @Schema(description = "场景值")
    private String secret;

}
