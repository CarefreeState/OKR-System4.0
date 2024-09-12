package cn.lbcmmszdntnt.domain.user.model.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.lbcmmszdntnt.domain.user.model.po.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 15:27
 */
@Schema(description = "微信小程序登录数据")
@Data
public class WxLoginDTO {

    @Schema(description = "code")
    @NotBlank(message = "code 不能为空")
    private String code;

    public User transToUser() {
        User user = new User();
        return user;
    }

    public static WxLoginDTO create(Map<?, ?> data) {
        return BeanUtil.mapToBean(data, WxLoginDTO.class, Boolean.FALSE, new CopyOptions());
    }

}
