package cn.lbcmmszdntnt.domain.core.model.vo.inner;

import cn.lbcmmszdntnt.domain.core.model.entity.inner.StatusFlag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-18
 * Time: 16:05
 */
@Data
@Schema(description = "用户状态指标列表信息")
public class UserStatusFlagsVO {

    @Schema(description = "用户 id")
    private Long userId;

    @Schema(description = "状态指标列表")
    private List<StatusFlag> statusFlags;

}
