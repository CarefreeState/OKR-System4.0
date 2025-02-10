package cn.bitterfree.domain.core.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2025-01-12
 * Time: 14:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "OKR 创建信息")
public class OKRCreateVO {

    @Schema(description = "OKR id")
    private Long id;

    @Schema(description = "OKR 内核 id")
    private Long coreId;

}
