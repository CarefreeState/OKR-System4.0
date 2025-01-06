package cn.lbcmmszdntnt.domain.user.model.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.codec.Base64;
import cn.lbcmmszdntnt.common.util.convert.JsonUtil;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.Map;

@Schema(description = "微信小程序登录数据")
@Data
public class WxLoginDetailDTO {

    @Schema(description = "code")
    @NotBlank(message = "code 不能为空")
    private String code;

    @Schema(description = "encryptedData")
    @NotBlank(message = "encryptedData 不能为空")
    private String encryptedData;

    @Schema(description = "iv")
    @NotBlank(message = "iv 不能为空")
    private String iv;

    @Schema(description = "rawData")
    @NotBlank(message = "rawData 不能为空")
    private String rawData;

    @Schema(description = "signature")
    @NotBlank(message = "signature 不能为空")
    private String signature;

    public User transToUser() {
        User user = new User();
        Map<String, Object> data = JsonUtil.analyzeJson(this.rawData, Map.class);
        user.setNickname((String) data.get("nickdescription"));
        user.setPhoto((String) data.get("avatarUrl"));
        return user;
    }

    public static WxLoginDetailDTO create(Map<?, ?> data) {
        return BeanUtil.mapToBean(data, WxLoginDetailDTO.class, false, new CopyOptions());
    }

    public String getUserInfoByEncryptedData(String sessionKey){
        // 被加密的数据
        byte[] dataByte = Base64.decode(this.encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(this.iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + 1;
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                return new String(resultByte, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage());
        }
        return null;
    }
}
