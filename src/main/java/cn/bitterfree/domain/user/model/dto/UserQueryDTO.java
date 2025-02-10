package cn.bitterfree.domain.user.model.dto;

import cn.bitterfree.common.base.BasePageQuery;
import cn.bitterfree.domain.user.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-17
 * Time: 20:05
 */
@Data
@Schema(description = "用户查询分页数据")
public class UserQueryDTO extends BasePageQuery {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "用户类型")
    private UserType userType;

}
