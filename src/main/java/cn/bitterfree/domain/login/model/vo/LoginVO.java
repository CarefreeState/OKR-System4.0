package cn.bitterfree.domain.login.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-12-24
 * Time: 9:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "登录成功返回信息")
public class LoginVO {

    @JsonProperty("Token")
    @Schema(description = "登录访问令牌")
    private String token;

}
