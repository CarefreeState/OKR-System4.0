package cn.lbcmmszdntnt.domain.qrcode.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-16
 * Time: 23:14
 */
@Schema(description = "小程序登录码")
@Builder
@Data
public class BindingQRCodeVO {

    @Schema(description = "小程序码地址")
    private String path;

    @Schema(description = "密钥")
    private String secret;

}
