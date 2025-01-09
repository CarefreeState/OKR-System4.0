package cn.lbcmmszdntnt.domain.qrcode.enums;

import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.domain.qrcode.constants.QRCodeConstants;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

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

    private final String type;
    private final String description;

    public static QRCodeType get(String name) {
        if(!StringUtils.hasText(name)) {
            return QRCodeConstants.DEFAULT_QRCODE_TYPE;
        }
        for(QRCodeType type : values()) {
            if(type.type.equals(name)) {
                return type;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.REQUEST_NOT_VALID);
    }

}
