package cn.bitterfree.api.domain.teaminvite.model.dto;

import cn.bitterfree.api.domain.qrcode.enums.QRCodeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-02-11
 * Time: 21:13
 */
@Data
@Schema(description = "团队要求信息")
public class TeamInviteDTO {

    @Schema(description = "邀请码类型")
    private QRCodeType qrCodeType;

}
