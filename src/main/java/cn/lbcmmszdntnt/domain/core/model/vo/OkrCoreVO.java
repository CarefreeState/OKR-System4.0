package cn.lbcmmszdntnt.domain.core.model.vo;


import cn.lbcmmszdntnt.domain.core.model.entity.OkrCore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:38
 */
@Schema(description = "OKR 内核详细数据")
@Data
public class OkrCoreVO extends OkrCore {

    @Schema
    private FirstQuadrantVO firstQuadrantVO;

    @Schema
    private SecondQuadrantVO secondQuadrantVO;

    @Schema
    private ThirdQuadrantVO thirdQuadrantVO;

    @Schema
    private FourthQuadrantVO fourthQuadrantVO;

}
