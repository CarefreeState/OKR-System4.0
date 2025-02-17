package cn.bitterfree.api.domain.user.model.vo;

import cn.bitterfree.api.common.base.BasePageResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-17
 * Time: 20:01
 */
@Data
@Schema(description = "用户查询分页信息")
public class UserQueryVO extends BasePageResult<UserVO> {

}
