package cn.bitterfree.api.domain.qrcode.enums;

import cn.bitterfree.api.common.enums.GlobalServiceStatusCode;
import cn.bitterfree.api.common.exception.GlobalServiceException;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-09
 * Time: 18:52
 */
@Getter
@AllArgsConstructor
@Schema(
        type = "string",
        description = "二维码类型 wx 微信小程序二维码、web 网页二维码",
        allowableValues = {"wx", "web"}
)
public enum QRCodeType {

    WX("wx", "微信小程序二维码"),
    WEB("web", "网页二维码"),

    ;

    @JsonValue
    private final String type;
    private final String description;

    public static QRCodeType get(String type) {
        for(QRCodeType qrCodeType : values()) {
            if(qrCodeType.type.equals(type)) {
                return qrCodeType;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.REQUEST_NOT_VALID);
    }

}